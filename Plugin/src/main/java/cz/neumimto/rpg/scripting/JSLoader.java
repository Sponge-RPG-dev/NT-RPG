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

import static cz.neumimto.rpg.Log.error;
import static cz.neumimto.rpg.Log.info;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.DebugLevel;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.configs.SkillsDefinition;
import cz.neumimto.rpg.skills.pipeline.SkillComponent;
import cz.neumimto.rpg.utils.FileUtils;
import jdk.internal.dynalink.beans.StaticClass;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.event.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * Created by NeumimTo on 13.3.2015.
 */
@Singleton
public class JSLoader {

	private static ScriptEngine engine;

	private static Path scripts_root = Paths.get(NtRpgPlugin.workingDir + "/scripts");
	private static Object listener;
	@Inject
	private IoC ioc;
	@Inject
	private ClassGenerator classGenerator;
	@Inject
	private ResourceLoader resourceLoader;
	@Inject
	private NtRpgPlugin ntRpgPlugin;
	@Inject
	private SkillService skillService;
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
				return;
			}
		} catch (Exception e) {
			error("Could not load script engine", e);
		}
	}

	public void loadNashorn()
			throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		Object fct = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory").newInstance();
		engine = (ScriptEngine) fct.getClass().getMethod("getScriptEngine", String[].class, ClassLoader.class)
				.invoke(fct, PluginConfig.JJS_ARGS.split(" "), Thread.currentThread().getContextClassLoader());
	}

	private void setup() {
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
			bindings.put("IoC", ioc);
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
				Object o = objectTypeEntry.getValue() == JsBinding.Type.CLASS ? objectTypeEntry.getKey() : objectTypeEntry.getKey().newInstance();
				bindings.put(objectTypeEntry.getKey().getSimpleName(), o);
			}
			dumpDocumentedFunctions(skillComponents);
			bindings.put("Folder", scripts_root.toString());
			bindings.put("GlobalScope", ioc.build(GlobalScope.class));
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
		Sponge.getGame().getEventManager().registerListeners(ioc.build(NtRpgPlugin.class), listener);
	}

	public void reloadSkills() {
		Invocable invocable = (Invocable) engine;
		try {
			invocable.invokeFunction("registerSkills");
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		File file = new File(NtRpgPlugin.workingDir, "Skills-Definition.conf");
		if (!file.exists()) {
			Asset asset = Sponge.getAssetManager().getAsset(ntRpgPlugin, "Skills-Definitions.conf").get();
			try {
				asset.copyToFile(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		info("Loading skills from file " + file.getName());
		try {
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
			ObjectMapper<SkillsDefinition> mapper = ObjectMapper.forClass(SkillsDefinition.class);
			HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(file.toPath()).build();
			SkillsDefinition definition = mapper.bind(new SkillsDefinition()).populate(hcl.load());
			definition.getSkills().stream()
					.map(a -> skillService.skillDefinitionToSkill(a, urlClassLoader))
					.forEach(a -> skillService.registerAdditionalCatalog(a));
		} catch (Exception e) {
			throw new RuntimeException("Could not load file " + file, e);
		}
	}

	public void reloadGlobalEffects() {
		Invocable invocable = (Invocable) engine;
		try {
			invocable.invokeFunction("registerGlobalEffects");
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void reloadAttributes() {
		Invocable invocable = (Invocable) engine;
		try {
			invocable.invokeFunction("registerAttributes");
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void generateListener() {
		Invocable invocable = (Invocable) engine;
		try {
			invocable.invokeFunction("generateListener");
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
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

