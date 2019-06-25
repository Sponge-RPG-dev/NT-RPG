package cz.neumimto.rpg.common.scripting;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import java.util.Map;
import java.util.Set;

public class BindingsHelper {

    private ScriptEngine scriptEngine;

    public BindingsHelper(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public Set<Map.Entry<String, Object>> getEngineScopeKeys() {
        return scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).entrySet();
    }

    public Bindings getGlobalScopeKeys() {
        return (Bindings) scriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE).entrySet();
    }
}
