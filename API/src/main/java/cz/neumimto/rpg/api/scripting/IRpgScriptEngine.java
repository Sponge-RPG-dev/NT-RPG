package cz.neumimto.rpg.api.scripting;

import cz.neumimto.rpg.api.skills.scripting.JsBinding;

import java.io.File;
import java.util.Map;

public interface IRpgScriptEngine {

    void prepareEngine();

    void loadInternalSkills();

    void loadSkillDefinitionFile(ClassLoader urlClassLoader, File confFile);

    Map<Class<?>, JsBinding.Type> getDataToBind();

    Object executeScript(String functionName, Object... args);

    Object executeScript(String functionName);

}
