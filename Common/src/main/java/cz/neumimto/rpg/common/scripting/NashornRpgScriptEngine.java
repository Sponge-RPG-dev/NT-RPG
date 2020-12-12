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

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.MultipleParentClassLoader;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Argument;

import javax.script.*;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;

/**
 * Created by NeumimTo on 13.3.2015.
 */
@SuppressWarnings("unchecked")
public class NashornRpgScriptEngine extends AbstractRpgScriptEngine {

    private static ScriptEngine engine;

    private CompiledScript lib;

    private ScriptContext scriptContext;


    @Override
    public void prepareEngine() {
        try {
            loadNashorn();
            if (engine != null) {
                setup();
                info("JS resources loaded.");
            }

            ScriptLib scriptLib = getLib();
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
            generateDynamicListener(eventListeners);

            reloadSkills();
        } catch (Exception e) {
            error("Could not load script engine", e);
        }
    }

    private void loadNashorn() throws Exception {
        Object fct = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory").newInstance();
        List<ClassLoader> list = new ArrayList<>();
        list.add(this.getClass().getClassLoader());
        list.addAll(resourceLoader.getClassLoaderMap().values());
        MultipleParentClassLoader multipleParentClassLoader = new MultipleParentClassLoader(list);
        engine = (ScriptEngine) fct.getClass().getMethod("getScriptEngine", String[].class, ClassLoader.class)
                .invoke(fct, (Rpg.get().getPluginConfig().JJS_ARGS + " --language=es6")
                        .split(" "), multipleParentClassLoader);
    }

    private void setup() {
        Path path = mergeScriptFiles();
        try (InputStreamReader rs = new InputStreamReader(new FileInputStream(path.toFile()))) {
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            bindings.put("Bindings", new BindingsHelper(engine));

            prepareBindings((s, o) -> {
                if (o instanceof Class) {
                    try {
                        engine.eval(((Class) o).getSimpleName() + " = Java.type(\"" +((Class) o).getCanonicalName() + "\")");
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    }
                } else {
                    bindings.put(s, o);
                }
            });

            if (Rpg.get().getPluginConfig().DEBUG.isDevelop()) {
                info("JSLOADER ====== Bindings", DebugLevel.DEVELOP);
                Map<String, Object> sorted = new TreeMap<>(bindings);
                for (Map.Entry<String, Object> e : sorted.entrySet()) {
                    info(e.getKey() + " -> " + e.getValue().toString(), DebugLevel.DEVELOP);
                }
                info("JSLOADER ====== Bindings", DebugLevel.DEVELOP);
            }

            this.scriptContext = engine.getContext();
            Compilable compilable = (Compilable) engine;
            lib = compilable.compile(rs);
            lib.eval();
        } catch (Throwable e) {
            Log.error("Unable to load script " + path, e);
        }
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

    private <T> T toInterface(JSObject object, Class<T> iface) {
        Invocable invocableEngine = (Invocable) lib.getEngine();
        return invocableEngine.getInterface(object, iface);
    }

    public CompiledScript getCompiledLib() {
        return lib;
    }

    public ScriptLib getLib() {
        JSObject libObject = (JSObject) scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).get("lib");
        return toInterface(libObject, ScriptLib.class);
    }

    public interface ScriptLib {
        Map<String, JSObject> getSkillHandlers();
        List<JSObject> getGlobalEffects();
        List<JSObject> getEventListeners();
    }

    public void generateDynamicListener(List<JSObject> list) {
        String name = "DynamicListener" + System.currentTimeMillis();

        DynamicType.Builder classBuilder = new ByteBuddy()
                .subclass(classGenerator.getListenerSubclass())
                .name(name);

        int i = 0;
        try {
            for (JSObject object : list) {
                if (!object.hasMember("consumer") || !((JSObject)object.getMember("consumer")).isFunction()) {
                    Log.warn("JS event listener missing function consumer, skipping");
                    continue;
                }
                //todo Why binding wont work here?
                //Consumer consumer = jsLoader.toInterface((JSObject) object.getMember("consumer"), Consumer.class);
                //jsLoader.toInterface((JSObject) o, Consumer.class);

                ScriptObjectMirror mirror = (ScriptObjectMirror) object.getMember("consumer");

                Consumer consumer = o -> mirror.call(mirror, o);


                String className = "";
                if (!object.hasMember("type")) {
                    Log.warn("Js event listener missing node type, skipping");
                    continue;
                }
                Object type1 = object.getMember("type");

                if (!(type1 instanceof CharSequence)) {
                    Log.warn("JS event listener for the event " + className + ", it's no longer needed to reference the class (Java.type(...)), use only the wrapped string");
                    continue;
                } else {
                    className = (String) type1;
                }

                Class<?> type = null;
                try {
                    type = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    Log.warn("JS event listener - unknown event " + className);
                    continue;
                }
                i++;
                String methodName = extract(object, "methodName", "on" + type.getSimpleName() + "" + i);

                DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition intercept = classBuilder
                        .defineMethod(methodName, void.class, Visibility.PUBLIC)
                        .withParameter(type)
                        .intercept(MethodDelegation.to(new EventHandlerInterceptor(consumer)));

                classBuilder = classGenerator.visitImplSpecAnnListener(intercept, object);
            }
            Class<?> loaded = classBuilder.make().load(getClass().getClassLoader()).getLoaded();
            Rpg.get().registerListeners(loaded.newInstance());
        } catch (Throwable t) {
            Log.error("Could not create a dynamic js listener", t);
        }
    }

    @Override
    public <T> T extract(Object o, String key, T def) {
        JSObject obj = (JSObject) o;
        return obj.hasMember(key) ? (T) obj.getMember(key) : def;
    }

    public static class EventHandlerInterceptor {
        private final Consumer consumer;

        public EventHandlerInterceptor(Consumer consumer) {
            this.consumer = consumer;
        }

        public void intercept(@Argument(0) Object object) {
            this.consumer.accept(object);
        }
    }
}

