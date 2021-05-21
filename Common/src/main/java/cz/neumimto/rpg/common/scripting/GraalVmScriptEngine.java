package cz.neumimto.rpg.common.scripting;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import org.graalvm.polyglot.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GraalVmScriptEngine extends AbstractRpgScriptEngine {

    private Engine engine;

    //not thread sync
    private Context context;

    private Value bindings;

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
        prepareBindings((s, o) -> context.getBindings("js").putMember(s, o));
        try {
            context.eval(Source.newBuilder("js", path.toFile()).build());
            bindings = context.getBindings("js");

            ScriptLib lib = getLib();

            Map<String, Map> skillHandlers = (Map<String, Map>) ((Object) lib.getSkillHandlers());
            if (skillHandlers != null) {
                for (Map.Entry<String, Map> e : skillHandlers.entrySet()) {
                    String key = e.getKey();
                    Map<String, Function<Object[], Object>> mapAndFnc = e.getValue();

                    SkillScriptHandlers handler = null;
                    if (mapAndFnc.containsKey("onCast")) {
                        handler = new GRLVM_Active(mapAndFnc.get("onCast"));
                    } else if (mapAndFnc.containsKey("castOnTarget")) {
                        handler = new GRLVM_Targetted(mapAndFnc.get("castOnTarget"));
                    } else if (mapAndFnc.containsKey("init")) {
                        handler = new GRLVM_Passive(mapAndFnc.get("init"));
                    } else {
                        Log.warn("unknown object " + key);
                        continue;
                    }

                    skillService.registerSkillHandler(key, handler);

                    int i = 0;
                }
            }

        } catch (IOException e) {
            Log.error("Could not read script file " + path, e);
        }
    }

    @Override
    public Object fn(String functionName, Object... args) {
        return bindings.execute(functionName).execute(args);
    }

    @Override
    public Object fn(String functionName) {
        return bindings.execute(functionName).execute();
    }

    @Override
    public <T> T eval(String expr, Class<T> t) {
        return context.eval(Source.create("js", expr)).as(t);
    }

    @Override
    public <T> T extract(Object o, String key, T def) {
        return null;
    }

    public Engine getEngine() {
        return engine;
    }


    protected ScriptLib getLib() {
        return context.getBindings("js").getMember("lib").as(ScriptLib.class);
    }


    static class GRLVM_Active implements SkillScriptHandlers.Active {
        private final Function<Object[], Object> fnc;

        GRLVM_Active(Function<Object[], Object> fnc) {
            this.fnc = fnc;
        }

        public SkillResult onCast(IActiveCharacter caster, PlayerSkillContext context) {
            return (SkillResult) fnc.apply(new Object[]{caster, context});
        }
    }

    static class GRLVM_Targetted implements SkillScriptHandlers.Targetted {
        private final Function<Object[], Object> fnc;

        GRLVM_Targetted(Function<Object[], Object> fnc) {
            this.fnc = fnc;
        }

        public SkillResult castOnTarget(IActiveCharacter caster, PlayerSkillContext context, IEntity target) {
            return (SkillResult) fnc.apply(new Object[]{caster, context, target});
        }
    }

    static class GRLVM_Passive implements SkillScriptHandlers.Passive {
        private final Function<Object[], Object> fnc;

        GRLVM_Passive(Function<Object[], Object> fnc) {
            this.fnc = fnc;
        }

        public SkillResult init(IActiveCharacter caster, PlayerSkillContext context) {
            return (SkillResult) fnc.apply(new Object[]{caster, context});
        }
    }

    public interface ScriptLib {
        Map<String, Value> getSkillHandlers();
        List<Value> getGlobalEffects();
        List<Value> getEventListeners();
    }

}
