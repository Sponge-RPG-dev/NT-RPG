package cz.neumimto.rpg.api.scripting;

import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import jdk.nashorn.api.scripting.JSObject;

import javax.script.ScriptEngine;
import java.io.File;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Map;

public interface IRpgScriptEngine {

    void prepareEngine();

    void loadSkillDefinitionFile(URLClassLoader urlClassLoader, File confFile);

    Map<Class<?>, JsBinding.Type> getDataToBind();

    Object executeScript(String functionName, Object... args);

    Object executeScript(String functionName);

    <T> T toInterface(JSObject object, Class<T> iface);

}
