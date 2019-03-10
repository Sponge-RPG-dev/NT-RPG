package cz.neumimto.rpg.jsplayground;

import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Assert;

import java.io.File;
import java.nio.file.Files;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public class NashornPlayground {


    public void test0() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("playground/test01.js").getFile());

        ScriptEngine scriptEngine = new NashornScriptEngineFactory().getScriptEngine("--optimistic-types=true"/*, "-d=bytecode/"*/);
        Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        byte[] bytes = Files.readAllBytes(file.toPath());
        Object eval = scriptEngine.eval(new String(bytes));
        ScriptObjectMirror mirror = (ScriptObjectMirror) scriptEngine.eval("array");
        StaticClass o = (StaticClass) mirror.get("0");
        StaticClass o1 = (StaticClass) mirror.get("1");
        Assert.assertSame(o1, o);
        Assert.assertSame(o1.getRepresentedClass(), o.getRepresentedClass());
        StaticClass o2 = (StaticClass) mirror.get("2");
        Assert.assertNotSame(o1, o2);
        int i = 0;
    }
}
