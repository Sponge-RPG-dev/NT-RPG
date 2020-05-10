package cz.neumimto.rpg.common.scripting;

import jdk.nashorn.api.scripting.JSObject;

import java.util.List;
import java.util.Map;

public interface ScriptLib {
    Map<String, JSObject> getSkillHandlers();
    List<JSObject> getGlobalEffects();
    List<JSObject> getEventListeners();
}
