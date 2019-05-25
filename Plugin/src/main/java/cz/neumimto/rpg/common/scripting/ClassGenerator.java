package cz.neumimto.rpg.common.scripting;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.List;

public interface ClassGenerator {
    Object generateDynamicListener(List<ScriptObjectMirror> list);
}
