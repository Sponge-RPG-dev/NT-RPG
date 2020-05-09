package cz.neumimto.rpg.jsplayground;

import cz.neumimto.rpg.api.effects.IGlobalEffect;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Precompilation {

    public static void main(String... args) throws Exception {
        URL topLevel = Precompilation.class.getClassLoader().getResource("script1.js");
        URL toLoad = Precompilation.class.getClassLoader().getResource("script2.js");

        String[] s = new String[] {toLoad.getFile()};

        ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine("--language=es6");
        Bindings bindings = new SimpleBindings();
        bindings.put("toLoad",s);

        ScriptContext scriptContext = new SimpleScriptContext();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

        FileReader fileReader = new FileReader(new File(topLevel.getFile()));
        CompiledScript compiled = ((Compilable) engine).compile(fileReader);
        compiled.eval(bindings);

        Invocable i = (Invocable) compiled.getEngine();
        JSObject o = (JSObject) scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).get("nashorn.global");
        JSObject mirror = (JSObject) o.getMember("lib");

        ScriptLib lib = i.getInterface(mirror, ScriptLib.class);

        List<JSObject> skillHandlers = lib.getSkillHandlers();
        JSObject object = skillHandlers.get(0);
        List<String> l = new ArrayList<>();
        SkillScriptHandler handler = i.getInterface(object, SkillScriptHandler.class);
        handler.onCast(l, new Object());
        System.out.println(l);

        List<JSObject> eventListeners = lib.getEventListeners();
        object = eventListeners.get(0);
        ScriptEventListener anInterface = i.getInterface(object, ScriptEventListener.class);
        System.out.println(anInterface.beforeModifications());
        System.out.println(anInterface.type());
        System.out.println(anInterface.order());
        System.out.println(anInterface.consumer() != null);

    }


    public interface SkillScriptHandler {
        void onCast(List<String> str, Object o);
    }

    public interface ScriptEventListener {
        String type();
        Consumer consumer();
        String order();
        boolean beforeModifications();

    }

    public interface ScriptLib {
        List<JSObject> getSkillHandlers();
        List<JSObject> getGlobalEffects();
        List<JSObject> getEventListeners();
    }

}
