package cz.neumimto.rpg.common.scripting;

import cz.neumimto.rpg.api.logging.Log;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;

import java.io.IOException;
import java.nio.file.Path;

public class GraalVmScriptEngine extends AbstractRpgScriptEngine {

    private Engine engine;

    //not thread sync
    private Context context;

    //I could use even tsc
    @Override
    public void prepareEngine() {
        engine = Engine.create();
        Context.Builder contextBuilder = Context.newBuilder()
                .allowExperimentalOptions(true)
                .allowHostClassLookup(s -> true)
                .option("js.nashorn-compat", "true")
                .allowHostAccess(HostAccess.ALL)
                .engine(engine);
        context = contextBuilder.build();
        Path path = mergeScriptFiles();
        prepareBindings((s, o) -> context.getBindings("js").putMember(s,o));
        try {
            context.eval(Source.newBuilder("js", path.toFile()).build());
        } catch (IOException e) {
            Log.error("Could not read script file " + path, e);
        }
    }

    @Override
    public Object executeScript(String functionName, Object... args) {
        return context.getBindings("js").getMember(functionName).execute(args);
    }

    @Override
    public Object executeScript(String functionName) {
        return context.getBindings("js").getMember(functionName).execute();
    }

    public Engine getEngine() {
        return engine;
    }
}
