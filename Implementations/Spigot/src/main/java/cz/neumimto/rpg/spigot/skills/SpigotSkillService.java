package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.nts.NTScript;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.types.ScriptSkill;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class SpigotSkillService extends SkillService {

    private Map<Character, SpigotSkillTreeInterfaceModel> guiModelByCharacter;

    private Map<Short, SpigotSkillTreeInterfaceModel> guiModelById;

    public SpigotSkillService() {
        guiModelByCharacter = new HashMap<>();
        guiModelById = new HashMap<>();
    }

    @Override
    public void load() {
        int i = 0;
        if (!SpigotRpgPlugin.testEnv) {
            for (String str : Rpg.get().getPluginConfig().SKILLTREE_GUI) {
                String[] split = str.split(",");

                short k = (short) (Short.MAX_VALUE - i);
                Material material = Material.matchMaterial(split[1]);
                material = material == null ? Material.STICK : material;

                SpigotSkillTreeInterfaceModel model = new SpigotSkillTreeInterfaceModel(Integer.parseInt(split[2]),
                        material, k);

                guiModelById.put(k, model);
                guiModelByCharacter.put(split[0].charAt(0), model);
                i++;
            }
        }
        //scriptSkillsParents.put("targeted", TargetedScriptSkill.class);
        super.load();
    }

    @Override
    public NTScript getNtScriptCompilerFor(Class<? extends SkillScriptHandlers> c) {
        return ntScriptEngine.prepareCompiler(builder -> {
            try {
                builder
                    .add(Vector.class.getConstructor(double.class, double.class, double.class), List.of("x","y","z"))

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
                    ;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, c);
    }

    @Override
    public ISkillTreeInterfaceModel getGuiModelByCharacter(char c) {
        return guiModelByCharacter.get(c);
    }

    @Override
    public ScriptSkill getSkillByHandlerType(SkillScriptHandlers instance) {
        if (instance instanceof SkillScriptHandlers.Targetted) {
            return new TargetedScriptSkill();
        }
        return super.getSkillByHandlerType(instance);
    }

    public SpigotSkillTreeInterfaceModel getGuiModelById(Short k) {
        return guiModelById.get(k);
    }
}
