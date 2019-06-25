package cz.neumimto.rpg.api.scripting;

import cz.neumimto.rpg.api.skills.scripting.JsBinding;

import javax.script.ScriptEngine;
import java.io.File;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

public interface IScriptEngine {
    ScriptEngine getEngine();

    void initEngine();

    void generateDynamicListener(List list);

    void reloadSkills();

    void loadSkillDefinitionFile(URLClassLoader urlClassLoader, File confFile);

    void reloadGlobalEffects();

    void reloadAttributes();

    void generateListener();

    Map<Class<?>, JsBinding.Type> getDataToBind();
}
