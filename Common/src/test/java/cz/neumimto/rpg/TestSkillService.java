package cz.neumimto.rpg;

import cz.neumimto.nts.NTScript;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.junit.TestDictionary;

import javax.inject.Singleton;
import java.io.File;
import java.util.List;

@Singleton
public class TestSkillService extends SkillService {

    @Override
    public NTScript getNtScriptCompilerFor(Class<? extends SkillScriptHandlers> c) {
        return ntScriptEngine.prepareCompiler(builder -> {
            try {
                builder
                        .add(Math.class.getDeclaredMethod("max", double.class, double.class), List.of("a","b"))
                        .add(Math.class.getDeclaredMethod("min", double.class, double.class), List.of("a","b"))
                        .add(Math.class.getDeclaredMethod("pow", double.class, double.class), List.of("a","b"))
                        .add(Math.class.getDeclaredMethod("abs", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("sqrt", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("ceil", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("floor", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("log", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("toDegrees", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("toRadians", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("random"), List.of("a"))

                        .add(Math.class.getDeclaredMethod("sin", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("sinh", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("asin", double.class), List.of("a"))

                        .add(Math.class.getDeclaredMethod("tan", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("tanh", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("atan", double.class), List.of("a"))

                        .add(Math.class.getDeclaredMethod("cos", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("cosh", double.class), List.of("a"))
                        .add(Math.class.getDeclaredMethod("acos", double.class), List.of("a"))
                        .debugOutput(Rpg.get().getWorkingDirectory() + File.separator + "/compiled-scripts")
                ;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, c);
    }

    @Override
    public ISkillTreeInterfaceModel getGuiModelByCharacter(char c) {
        return null;
    }

    @Override
    public ISkill getSkillById(String id) {
        if (id.startsWith("ntrpg")) {
            return TestDictionary.DUMMY_SKILL;
        }
        return super.getSkillById(id);
    }
}
