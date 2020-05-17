package cz.neumimto.rpg.common.scripting;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.management.ExecutionListener;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.junit.jupiter.api.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class GraalVmScriptEngineTest {

    public static void main(String[]a) throws Exception {
        Map map = new HashMap();


        ScriptEngine engineByName = new ScriptEngineManager().getEngineByName("graal.js");

        try (Context context = Context.newBuilder().allowExperimentalOptions(true).option("js.nashorn-compat", "true").build()) {
            context.eval("js", "print(Java.type('java.lang.Math').PI)");
        }

        Context ctx = Context.newBuilder()
                .allowExperimentalOptions(true)
                .allowHostAccess(HostAccess.ALL)
                .option("js.nashorn-compat", "true")
                .build().create();

        Value binding = ctx.getPolyglotBindings();

        binding.putMember("ArrayList", ArrayList.class);

        //ExecutionListener listener = ExecutionListener.newBuilder()
        //        .onEnter((e) -> System.out.println(
        //                e.getLocation().getCharacters()))
        //        .statements(true)
        //        .attach(ctx.getEngine());

        Source src = Source.newBuilder("js",
                "var BigInteger = Java.type('java.math.BigInteger');console.log(BigInteger.valueOf(2).pow(100).toString(16));let list = new ArrayList();list.add(\"1\")", null)
                .build();

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

    public static String SOURCE =
            "\n" +
            "let events = new ArrayList();events.add(\"1\")\n" +
            "let globalEffects = new ArrayList();\n" +
            "let skillHandlers = new HashMap();\n" +
            "\n" +
            "let log = function(obj) {\n" +
            "    log.info(obj);\n" +
            "}\n" +
            "\n" +
            "let registerGlobalEffect = function(obj) {\n" +
            "    globalEffects.add(obj);\n" +
            "}\n" +
            "\n" +
            "let registerSkillHandler = function(id,obj) {\n" +
            "    if (skillHandlers.containsKey(id)) {\n" +
            "        log(\"Multiple scripts attempted to register skill handler id \" + id + \" will be skipped.\")\n" +
            "    } else {\n" +
            "        skillHandlers.put(id,obj);\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "let defineCharacterProperty = function(name, def) {\n" +
            "    var lastid = playerPropertyService.LAST_ID;\n" +
            "    lastid++;\n" +
            "    if (name !== null) {\n" +
            "        playerPropertyService.registerProperty(name, lastid);\n" +
            "    }\n" +
            "    if (def !== null) {\n" +
            "        playerPropertyService.registerDefaultValue(lastid, def);\n" +
            "    }\n" +
            "    playerPropertyService.LAST_ID = lastid;\n" +
            "    return lastid;\n" +
            "}\n" +
            "\n" +
            "let registerEventListener = function(eventData) {\n" +
            "    if (eventData == null) {\n" +
            "        log(\"Could not register Event listener defined via JS, parametr EventData is null\")\n" +
            "        return;\n" +
            "    }\n" +
            "    if (eventData.consumer == null) {\n" +
            "        log(\"Could not register Event listener defined via JS, parametr EventData.consumer is null\")\n" +
            "        return;\n" +
            "    }\n" +
            "    if (eventData.type == null) {\n" +
            "        log(\"Could not register Event listener defined via JS, parametr EventData.type is null\")\n" +
            "        return;\n" +
            "    }\n" +
            "    events.add(eventData);\n" +
            "}\n" +
            "\n" +
            "var lib = {\n" +
            "    getEventListeners: function() {\n" +
            "        return events;\n" +
            "    },\n" +
            "    getGlobalEffects: function() {\n" +
            "        return globalEffects;\n" +
            "    },\n" +
            "    getSkillHandlers: function() {\n" +
            "        log(skillHandlers.size());\n" +
            "        return skillHandlers;\n" +
            "    }\n" +
            "}\n";

}