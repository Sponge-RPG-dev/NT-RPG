package cz.neumimto.rpg.jsplayground;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

public class Precompilation {

    static String FUNCTIONS2 =
            "function hello( arg ) {" +
                    "  return 'Hello ' + arg;" +
                    "};" +
                    "function () {" +
                    "  return { 'hello': hello };" +
                    "};";


    public static void main(String... args) throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName( "Nashorn" );

        CompiledScript compiled = ((Compilable) engine).compile(FUNCTIONS2);
        ScriptObjectMirror lastFunction = (ScriptObjectMirror)compiled.eval();
        ScriptObjectMirror functionTable = (ScriptObjectMirror)lastFunction.call( null );
        System.out.println( functionTable.callMember( "hello", "world" ) );


        URL topLevel = Precompilation.class.getClassLoader().getResource("script1.js");
        URL toLoad = Precompilation.class.getClassLoader().getResource("script2.js");
        URL toLoad2 = Precompilation.class.getClassLoader().getResource("script3.js");

        String[] s = new String[] {toLoad.getFile(), toLoad2.getFile()};

        engine = new NashornScriptEngineFactory().getScriptEngine("--language=es6");
        Bindings bindings = new SimpleBindings();
        bindings.put("toLoad",s);
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);

        FileReader fileReader = new FileReader(new File(topLevel.getFile()));
        compiled = ((Compilable) engine).compile(fileReader);
        lastFunction = (ScriptObjectMirror)compiled.eval();
        functionTable = (ScriptObjectMirror)lastFunction.call( null );
        String[] functionNames = functionTable.getOwnKeys( true );

        System.out.println( "Function names: " + Arrays.toString( functionNames ) );
        functionTable.callMember( "test2");


    }


}
