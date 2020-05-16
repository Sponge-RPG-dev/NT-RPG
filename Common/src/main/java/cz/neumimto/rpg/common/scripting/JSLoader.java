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

package cz.neumimto.rpg.common.scripting;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.inject.Injector;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.SkillsDefinition;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;
import jdk.nashorn.api.scripting.JSObject;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.script.*;

/**
 * Created by NeumimTo on 13.3.2015.
 */
@Singleton
public class JSLoader implements IScriptEngine {

    private static ScriptEngine engine;

    private static Path scripts_root;

    private static Object listener;

    @Inject
    private Injector injector;

    @Inject
    private ClassGenerator classGenerator;

    @Inject
    private ResourceLoader resourceLoader;

    @Inject
    private SkillService skillService;

    @Inject
    private AssetService assetService;

    private CompiledScript lib;

    private Map<Class<?>, JsBinding.Type> dataToBind = new HashMap<>();
    private ScriptContext scriptContext;

    @Override
    public ScriptEngine getEngine() {
        return engine;
    }

    @Override
    public void initEngine() {
        try {
            scripts_root = Paths.get(Rpg.get().getWorkingDirectory() + "/scripts");
            FileUtils.createDirectoryIfNotExists(scripts_root);
            loadNashorn();
            if (engine != null) {
                setup();
                info("JS resources loaded.");
            }
            JSObject libObject = (JSObject) scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).get("lib");
            ScriptLib scriptLib = toInterface(libObject, ScriptLib.class);
            Map<String, JSObject> skillHandlers = scriptLib.getSkillHandlers();

            for (Map.Entry<String, JSObject> entry : skillHandlers.entrySet()) {
                JSObject value = entry.getValue();
                Class<? extends SkillScriptHandlers> handlers = null;
                if (value.hasMember("onCast") && ((JSObject) value.getMember("onCast")).isFunction()) {
                    handlers = SkillScriptHandlers.Active.class;
                } else if (value.hasMember("castOnTarget") && ((JSObject) value.getMember("castOnTarget")).isFunction()) {
                    handlers = SkillScriptHandlers.Targetted.class;
                } else if (value.hasMember("init") && ((JSObject) value.getMember("init")).isFunction()) {
                    handlers = SkillScriptHandlers.Passive.class;
                } else {
                    Log.warn("unknown object " + value.toString());
                    continue;
                }
                skillService.registerSkillHandler(entry.getKey(), toInterface(entry.getValue(), handlers));
            }

            List<JSObject> globalEffects = scriptLib.getGlobalEffects();
            for (JSObject globalEffect : globalEffects) {
                //todo
            }

            List<JSObject> eventListeners = scriptLib.getEventListeners();
            classGenerator.generateDynamicListener(eventListeners);

            reloadSkills();
        } catch (Exception e) {
            error("Could not load script engine", e);
        }
    }

    @Override
    public void loadNashorn() throws Exception {
        Object fct = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory").newInstance();
        List<ClassLoader> list = new ArrayList<>();
        list.add(this.getClass().getClassLoader());
        list.addAll(resourceLoader.getClassLoaderMap().values());
        MultipleParentClassLoader multipleParentClassLoader = new MultipleParentClassLoader(list);
        engine = (ScriptEngine) fct.getClass().getMethod("getScriptEngine", String[].class, ClassLoader.class)
                .invoke(fct, (Rpg.get().getPluginConfig().JJS_ARGS + " --language=es6")
                        .split(" "), multipleParentClassLoader);
    }

    private Path mergeScriptFiles() {
        Path path = Paths.get(scripts_root + File.separator + ".deployed.js");
        if (path.toFile().exists()) {
            path.toFile().delete();
        }

        final StringBuilder bigChunkOfCode = new StringBuilder();
        bigChunkOfCode.append(assetService.getAssetAsString("Main.js")).append(System.lineSeparator());

        try {
            Queue<Path> directories = new PriorityQueue<>();
            directories.add(scripts_root);
            while (directories.size() != 0) {
                Files.list(directories.poll()).forEach((file -> {
                    if (file.toFile().isDirectory()) directories.add(file);
                    else if (file.toFile().getName().endsWith(".js")) {
                        bigChunkOfCode.append(System.lineSeparator()).append("// ").append(file.toFile().getName()).append(System.lineSeparator());
                        try {
                            for (String line : Files.readAllLines(file))
                                bigChunkOfCode.append(line).append(System.lineSeparator());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }

            return Files.write(path, bigChunkOfCode.toString().getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException("Could not write .deployed.js ", e);
        }
    }

    private void setup() {
        Path path = mergeScriptFiles();
        List<SkillComponent> skillComponents = new ArrayList<>();
        try (InputStreamReader rs = new InputStreamReader(new FileInputStream(path.toFile()))) {
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("Injector", injector);
            bindings.put("Folder", scripts_root);
            bindings.put("Rpg", Rpg.get());
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
            if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
                info("JSLOADER ====== Bindings");
                Map<String, Object> sorted = new TreeMap<>(bindings);
                for (Map.Entry<String, Object> e : sorted.entrySet()) {
                    info(e.getKey() + " -> " + e.getValue().toString());
                }
                info("===== Bindings END =====");
            }

            this.scriptContext = engine.getContext();
            Compilable compilable = (Compilable) engine;
            lib = compilable.compile(rs);
            lib.eval();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void dumpDocumentedFunctions(List<SkillComponent> skillComponents) {
        File file = new File(Rpg.get().getWorkingDirectory(), "functions.md");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            for (SkillComponent skillComponent : skillComponents) {
                String s = assetService.getAssetAsString("templates/function.md");
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

    public void reloadSkills() {
        Path addonDir = Paths.get(Rpg.get().getWorkingDirectory() + File.separator + "addons");
        File file = addonDir.resolve("Skills-Definition.conf").toFile();
        if (!file.exists()) {
            assetService.copyToFile("Skills-Definitions.conf", file.toPath());
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

        File file1 = addonDir.toFile();
        File[] files = file1.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".conf"));
        if (files != null) {
            for (File confFile : files) {
                info("Loading file " + confFile);
                loadSkillDefinitionFile(urlClassLoader, confFile);
            }
        }
    }

    @Override
    public void loadSkillDefinitionFile(URLClassLoader urlClassLoader, File confFile) {
        info("Loading skills from file " + confFile.getName());
        try (FileConfig fc = FileConfig.of(confFile.getPath())) {
            fc.load();
            SkillsDefinition definition = new ObjectConverter().toObject(fc, SkillsDefinition::new);
            definition.getSkills().stream()
                    .map(a -> skillService.skillDefinitionToSkill(a, urlClassLoader))
                    .forEach(a -> skillService.registerAdditionalCatalog(a));
        } catch (Exception e) {
            Log.error("Could not load file " + confFile, e);
        }
    }

    @Override
    public Map<Class<?>, JsBinding.Type> getDataToBind() {
        return dataToBind;
    }

    @Override
    public Object executeScript(String functionName, Object... args) {
        try {
            Invocable invocableEngine = (Invocable) lib.getEngine();
            return invocableEngine.invokeFunction(functionName, args);
        } catch (ScriptException | NoSuchMethodException e) {
            throw new ScriptExecutionException(" Could not execute the script function/method " + functionName, e);
        }
    }

    @Override
    public Object executeScript(String functionName) {
        try {
            Invocable invocableEngine = (Invocable) lib.getEngine();
            return invocableEngine.invokeFunction(functionName);
        } catch (ScriptException | NoSuchMethodException e) {
            throw new ScriptExecutionException(" Could not execute the script function/method " + functionName, e);
        }
    }

    @Override
    public <T> T toInterface(JSObject object, Class<T> iface) {
        Invocable invocableEngine = (Invocable) lib.getEngine();
        return invocableEngine.getInterface(object, iface);
    }

    private static class ScriptExecutionException extends RuntimeException {

        public ScriptExecutionException(String message, Throwable cause) {
            super(message, cause);
        }

    }

}

