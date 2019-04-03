/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.scripting;

import com.google.inject.Injector;
import cz.neumimto.rpg.*;
import cz.neumimto.rpg.configuration.DebugLevel;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.configs.SkillsDefinition;
import cz.neumimto.rpg.skills.pipeline.SkillComponent;
import cz.neumimto.rpg.utils.FileUtils;
import jdk.internal.dynalink.beans.StaticClass;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.event.Event;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;

import static cz.neumimto.rpg.Log.error;
import static cz.neumimto.rpg.Log.info;
import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 13.3.2015.
 */
@Singleton
public class JSLoader {

	private static ScriptEngine engine;

	private static Path scripts_root = Paths.get(NtRpgPlugin.workingDir + "/scripts");

	private static Object listener;

	@Inject
	private Injector injector;

	@Inject
	private ClassGenerator classGenerator;

	@Inject
	private ResourceLoader resourceLoader;

	@Inject
	private NtRpgPlugin ntRpgPlugin;

	@Inject
	private SkillService skillService;

	@Inject
	private GlobalScope globalScope;

	private Map<Class<?>, JsBinding.Type> dataToBind = new HashMap<>();

	public static ScriptEngine getEngine() {
		return engine;
	}

	public void initEngine() {
		try {
			FileUtils.createDirectoryIfNotExists(scripts_root);
			loadNashorn();
			if (engine != null) {
				setup();
				reloadGlobalEffects();
				reloadSkills();
				reloadAttributes();
				generateListener();
				info("JS resources loaded.");
			}
		} catch (Exception e) {
			error("Could not load script engine", e);
		}
	}

	public void loadNashorn()
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		Object fct = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory").newInstance();
		List<ClassLoader> list = new ArrayList<>();
		list.add(this.getClass().getClassLoader());
		list.addAll(resourceLoader.getClassLoaderMap().values());
		MultipleParentClassLoader multipleParentClassLoader = new MultipleParentClassLoader(list);
		engine = (ScriptEngine) fct.getClass().getMethod("getScriptEngine", String[].class, ClassLoader.class)
				.invoke(fct, pluginConfig.JJS_ARGS.split(" "), multipleParentClassLoader);
	}

	private <T extends CatalogType> void setup() {
		Path path = Paths.get(scripts_root + File.separator + "Main.js");
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("Main.js")) {
				Files.copy(resourceAsStream, path);
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		List<SkillComponent> skillComponents = new ArrayList<>();
		try (InputStreamReader rs = new InputStreamReader(new FileInputStream(path.toFile()))) {
			Bindings bindings = new SimpleBindings();
			bindings.put("Injector", injector);
			bindings.put("Bindings", new BindingsHelper(engine));
			for (Map.Entry<Class<?>, JsBinding.Type> objectTypeEntry : dataToBind.entrySet()) {
				if (objectTypeEntry.getValue() == JsBinding.Type.CONTAINER) {
					for (Field field : objectTypeEntry.getKey().getDeclaredFields()) {
						field.setAccessible(true);
						if (field.isAnnotationPresent(SkillComponent.class)) {
							Object o = field.get(null);
							String name = field.getName();
							bindings.put(name.toLowerCase(), o);
							skillComponents.add(field.getAnnotation(SkillComponent.class));
						}
					}
					continue;
				}
				if (objectTypeEntry.getValue() == JsBinding.Type.CLASS) {
					bindings.put(objectTypeEntry.getKey().getSimpleName(),
							engine.eval("Java.type(\"" + objectTypeEntry.getKey().getCanonicalName() + "\");"));
					continue;
				}
				if (objectTypeEntry.getValue() == JsBinding.Type.OBJECT) {
					if (objectTypeEntry.getKey().isAnnotationPresent(SkillComponent.class)) {
						skillComponents.add(objectTypeEntry.getKey().getAnnotation(SkillComponent.class));
						bindings.put(objectTypeEntry.getKey().getSimpleName().toLowerCase(), objectTypeEntry.getKey().newInstance());
					} else {
						bindings.put(objectTypeEntry.getKey().getSimpleName(), objectTypeEntry.getKey().newInstance());
					}
				}
			}
			dumpDocumentedFunctions(skillComponents);
			bindings.put("Folder", scripts_root);
			bindings.put("GlobalScope", globalScope);
			if (pluginConfig.DEBUG.isDevelop()) {
				info("JSLOADER ====== Bindings");
				Map<String, Object> sorted = new TreeMap<>(bindings);
				for (Map.Entry<String, Object> e : sorted.entrySet()) {
					info(e.getKey() + " -> " + e.getValue().toString());
				}
				info("===== Bindings END =====");
			}
			engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
			engine.eval(rs);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void dumpDocumentedFunctions(List<SkillComponent> skillComponents) {
		File file = new File(NtRpgPlugin.workingDir, "functions.md");
		if (file.exists()) {
			file.delete();
		}

		Optional<Asset> asset = Sponge.getAssetManager().getAsset(ntRpgPlugin, "templates/function.md");
		Asset a = asset.get();
		try {
			file.createNewFile();

			for (SkillComponent skillComponent : skillComponents) {
				String s = a.readString();
				s = s.replaceAll("\\{\\{function\\.name}}", skillComponent.value());
				s = s.replaceAll("\\{\\{function\\.usage}}", skillComponent.usage());

				StringBuilder buffer = new StringBuilder();
				for (SkillComponent.Param param : skillComponent.params()) {
					buffer.append("    * ").append(param.value()).append("\n");
				}
				s = s.replaceAll("\\{\\{function\\.params}}", buffer.toString());
				Files.write(file.toPath(), s.getBytes(), StandardOpenOption.APPEND);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void generateDynamicListener(Map<StaticClass, Set<Consumer<? extends Event>>> set) {
		if (listener != null) {
			info("Found JS listener: " + listener.getClass().getSimpleName() + " Unregistering");
			Sponge.getGame().getEventManager().unregisterListeners(listener);
		}
		listener = classGenerator.generateDynamicListener(set);
		info("Registering js listener: " + listener.getClass().getSimpleName());
		Sponge.getGame().getEventManager().registerListeners(ntRpgPlugin, listener);
	}

	public void reloadSkills() {
		Invocable invocable = (Invocable) engine;
		try {
			invocable.invokeFunction("registerSkills");
		} catch (ScriptException | NoSuchMethodException e) {
			Log.error("Could not invoke JS function registerSkills()", e);
		}
		File file = new File(ResourceLoader.addonDir, "Skills-Definition.conf");
		if (!file.exists()) {
			Asset asset = Sponge.getAssetManager().getAsset(ntRpgPlugin, "Skills-Definitions.conf").get();
			try {
				asset.copyToFile(file.toPath());
			} catch (IOException e) {
				Log.error("Could not copy file Skills-Definition.conf into the directory " + ResourceLoader.addonDir, e);
			}
		}

		URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{}, this.getClass().getClassLoader()) {
			@Override
			public String toString() {
				return "Internal - " + System.currentTimeMillis();
			}

			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				info("Removing URLClassloader " + toString(), DebugLevel.DEVELOP);
			}
		};


		for (File confFile : ResourceLoader.addonDir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".conf"))) {
			info("Loading skills from file " + confFile.getName());
			try {
				ObjectMapper<SkillsDefinition> mapper = ObjectMapper.forClass(SkillsDefinition.class);
				HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(confFile.toPath()).build();
				SkillsDefinition definition = mapper.bind(new SkillsDefinition()).populate(hcl.load());
				definition.getSkills().stream()
						.map(a -> skillService.skillDefinitionToSkill(a, urlClassLoader))
						.forEach(a -> skillService.registerAdditionalCatalog(a));
			} catch (Exception e) {
				throw new RuntimeException("Could not load file " + confFile, e);
			}
		}
	}

	public void reloadGlobalEffects() {
		Invocable invocable = (Invocable) engine;
		try {
			invocable.invokeFunction("registerGlobalEffects");
		} catch (ScriptException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void reloadAttributes() {
		Invocable invocable = (Invocable) engine;
		try {
			invocable.invokeFunction("registerAttributes");
		} catch (ScriptException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void generateListener() {
		Invocable invocable = (Invocable) engine;
		try {
			invocable.invokeFunction("generateListener");
		} catch (ScriptException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public Map<Class<?>, JsBinding.Type> getDataToBind() {
		return dataToBind;
	}

	public static class BindingsHelper {

		private ScriptEngine scriptEngine;

		public BindingsHelper(ScriptEngine scriptEngine) {
			this.scriptEngine = scriptEngine;
		}

		public ScriptEngine getScriptEngine() {
			return scriptEngine;
		}

		public Set<Map.Entry<String, Object>> getEngineScopeKeys() {
			return scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).entrySet();
		}

		public Bindings getGlobalScopeKeys() {
			return (Bindings) scriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE).entrySet();
		}
	}


}

