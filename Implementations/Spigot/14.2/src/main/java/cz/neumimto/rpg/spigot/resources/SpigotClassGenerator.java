package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class SpigotClassGenerator extends ClassGenerator {

    @Override
    public Object generateDynamicListener(List<ScriptObjectMirror> list) {
        return null;
    }
}
