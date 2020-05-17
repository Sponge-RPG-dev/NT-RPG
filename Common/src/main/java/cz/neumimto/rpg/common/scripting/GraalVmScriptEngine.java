package cz.neumimto.rpg.common.scripting;

import cz.neumimto.rpg.api.logging.Log;
import jdk.nashorn.api.scripting.JSObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
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
        Context.Builder contextBuilder = Context.newBuilder("js");
        contextBuilder.allowAllAccess(true);
        prepareBindings((s, o) -> context.getBindings("js").putMember(s,o));
        engine = Engine.create();
        contextBuilder.engine(engine);
        context = contextBuilder.build();
        Path path = mergeScriptFiles();
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
        return context.getBindings("js").getMember(functionName);
    }

    @Override
    public <T> T toInterface(JSObject object, Class<T> iface) {
        return null;
    }

    public Engine getEngine() {
        return engine;
    }
}
