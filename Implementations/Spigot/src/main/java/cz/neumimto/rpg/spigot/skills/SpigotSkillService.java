package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.nts.NTScript;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.common.skills.types.ScriptSkill;
import cz.neumimto.rpg.spigot.bridges.DatapackManager;
import cz.neumimto.rpg.spigot.effects.common.*;
import cz.neumimto.rpg.spigot.gui.inventoryviews.GuiConfig;
import cz.neumimto.rpg.spigot.gui.inventoryviews.SkillTreeViewBuilder;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Singleton
public class SpigotSkillService extends SkillService {

    @Inject
    private DatapackManager itemResolver;



    public static Consumer<NTScript.Builder> SPIGOT_SCRIPT_SCOPE;

    private static GuiConfig skilltreeGuiConfig;;

    static {
        SPIGOT_SCRIPT_SCOPE = builder -> {
            try {
                builder
                        .withEnum(Particle.class)
                        .withEnum(Sound.class)
                        .withEnum(BlockFace.class)
                        .withEnum(SkillResult.class)
                        .add(Vector.class.getConstructor(double.class, double.class, double.class), "vector", List.of("x", "y", "z"))

                        .add(Math.class.getDeclaredMethod("max", double.class, double.class), List.of("a", "b"))
                        .add(Math.class.getDeclaredMethod("min", double.class, double.class), List.of("a", "b"))
                        .add(Math.class.getDeclaredMethod("pow", double.class, double.class), List.of("a", "b"))
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
                        .add(Rpg.get().getScriptEngine().STL)
                        .debugOutput(Rpg.get().getWorkingDirectory() + File.separator + "/compiled-scripts");

                ;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
    }


    @Override
    public Consumer<NTScript.Builder> getNTSBuilderContext() {
        return SPIGOT_SCRIPT_SCOPE;
    }

    @Override
    public void load() {
        //java service provider bug
        ntScriptEngine.STL.addAll(Arrays.asList(
                BleedingEffect.class,
                FeatherFall.class,
                Maim.class,
                ManaShieldEffect.class,
                NoAutohealEffect.class,
                PiggifyEffect.class,
                SlowEffect.class,
                StunEffect.class,
                UnhealEffect.class,
                UnlimtedFoodLevelEffect.class,
                WebEffect.class,
                FlickerEffect.class,
                InvisibilityEffect.class,
                ArrowblastEffect.class
        ));

        super.load();
        skillTrees.putAll(skillTreeDao.getAll());
    }

    @Override
    protected Class getScriptTargetType(Class c, String s) {
        if ("tb".equalsIgnoreCase(s) || "TargetedBlock".equalsIgnoreCase(s)) {
            return SpigotSkillScriptHandlers.TargetedBlock.class;
        }
        return super.getScriptTargetType(c, s);
    }


    @Override
    public ScriptSkill getSkillByHandlerType(SkillScriptHandlers instance) {
        if (instance instanceof SkillScriptHandlers.Targetted) {
            return new TargetedScriptSkill();
        }
        if (instance instanceof SpigotSkillScriptHandlers.TargetedBlock) {
            return new TargetedBlockScriptSkill();
        }
        return super.getSkillByHandlerType(instance);
    }

    //todo fix this
    @Override
    public ISkillTreeInterfaceModel getGuiModelByCharacter(char c) {
        return SkillTreeViewBuilder.getGuiModelByCharacter(c);
    }

    @Override
    public void loadSkilltree(Runnable r) {
        SkillTreeViewBuilder.loadLater(r);
    }
}
