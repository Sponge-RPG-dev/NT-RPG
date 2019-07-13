package cz.neumimto.rpg.common.bytecode;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.List;

public abstract class ClassGenerator {

    public abstract Object generateDynamicListener(List<ScriptObjectMirror> list);

}
