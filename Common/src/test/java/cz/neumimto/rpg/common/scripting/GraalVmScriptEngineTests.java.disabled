package cz.neumimto.rpg.common.scripting;

import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.management.ExecutionListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.io.IOUtil;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
class GraalVmScriptEngineTests {

    static int i = 0;

    public static Runnable TEST = () -> i++;

    @Inject
    private GraalVmScriptEngineTest scriptEngine;

    public static void main(String[]a) {
        Map map = new HashMap();

        Context ctx = Context.newBuilder()
                .allowExperimentalOptions(true)
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(s -> true)
                .option("js.nashorn-compat", "true")
                .build().create();

        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {

            context.getBindings("js").putMember("ArrayList", ArrayList.class);
            context.getBindings("js").putMember("fnc", TEST);
            Source js = Source.create("js", "var ArrayList = Java.type('java.util.ArrayList');" +
                    "var list = new ArrayList();" +
                    "fnc();" +
                    "list.add(1); list");

            java.util.ArrayList v = context.eval(js).asHostObject();
            System.out.println(v.get(0));
            assert v.get(0).equals(1);
        }


        ScriptEngine engineByName = new ScriptEngineManager().getEngineByName("graal.js");




        try (Context context = Context.newBuilder().allowExperimentalOptions(true).allowHostClassLookup(s -> true).option("js.nashorn-compat", "true").allowHostAccess(HostAccess.ALL).build()) {
            context.eval("js", "print(Java.type('java.lang.Math').PI)");
        } catch (Exception e) {
            e.printStackTrace();
        }



        Value binding = ctx.getPolyglotBindings();
        binding.putMember("ArrayList", ArrayList.class);
        ctx.eval("js", "var list = new ArrayList();list.add(\"1\");");
        ExecutionListener.newBuilder()
                .onEnter((e) -> System.out.println(
                        e.getLocation().getCharacters()))
                .statements(true)
                .attach(ctx.getEngine());

        Source src = null;
        try {
            src = Source.newBuilder("js",
                    "var BigInteger = Java.type('java.math.BigInteger');console.log(BigInteger.valueOf(2).pow(100).toString(16));let list = new ArrayList();list.add(\"1\")", null)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ctx.eval(src);
        //listener.close();
        List list = binding.getMember("list").as(List.class);
        assert list.size() == 1;
        Value procFn = binding.getMember("log");

        ZonedDateTime start = ZonedDateTime.now();
        for (int i = 0; i < 1_000_000; i++) {
            map.put("index"+i, i);
            Value result2 = procFn.execute("Log from js", ctx);
        }

        long time = start.until(ZonedDateTime.now(), ChronoUnit.MILLIS);
        System.out.println("testGraalPolyglotPerformance took "+time+" millis");
        ctx.close();
    }


    @Test
    public void testGraalVmLoading() {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("assets/nt-rpg/Main.js");
        Collection<String> strings = IOUtil.readLines(resourceAsStream);
        scriptEngine.prepareEngine();
    }

    public static class GraalVmScriptEngineTest extends GraalVmScriptEngine {
        @Override
        protected Path mergeScriptFiles() {
            URL resource = getClass().getClassLoader().getResource("assets/nt-rpg/Main.js");
            try {
                return Paths.get(resource.toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}