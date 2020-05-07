package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public interface ScriptSkill<T> {

    default void initScript() {
        ScriptSkillModel model = getModel();
        String s = bindScriptToTemplate(model);
        try {
            ScriptEngine engine = Rpg.get().getScriptEngine().getEngine();
            Compilable c = (Compilable) engine;
            CompiledScript compile = c.compile(s);
            setScript(compile);
        } catch (ScriptException e) {
            Log.error("Could not init script ", e);
        }
    }

    void setScript(CompiledScript compile);

    void setExecutor(T ses);

    ScriptSkillModel getModel();

    void setModel(ScriptSkillModel model);

    default String bindScriptToTemplate(ScriptSkillModel model) {
        String s = Rpg.get().getTextAssetContent(getTemplateName());
        s = s.replaceAll("\\{\\{skill\\.id}}", model.getId().replaceAll(":", ""));
        s = s.replaceAll("\\{\\{userScript}}", model.getScript());
        s = fill(s);
        return s;
    }

    String getTemplateName();

    default String fill(String asset) {
        return asset;
    }
}
