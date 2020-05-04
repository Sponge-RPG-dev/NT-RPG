package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.skills.AbstractSkillService;
import org.bukkit.Material;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class SpigotSkillService extends AbstractSkillService {

    private Map<Character, SpigotSkillTreeInterfaceModel> guiModelByCharacter;

    private Map<Short, SpigotSkillTreeInterfaceModel> guiModelById;

    public SpigotSkillService() {
        guiModelByCharacter = new HashMap<>();
        guiModelById = new HashMap<>();
    }

    @Override
    public void load() {
        int i = 0;

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
        //  scriptSkillsParents.put("targeted", TargetedScriptSkill.class);
        super.load();
    }

    @Override
    public ISkillTreeInterfaceModel getGuiModelByCharacter(char c) {
        return guiModelByCharacter.get(c);
    }


    public SpigotSkillTreeInterfaceModel getGuiModelById(Short k) {
        return guiModelById.get(k);
    }
}
