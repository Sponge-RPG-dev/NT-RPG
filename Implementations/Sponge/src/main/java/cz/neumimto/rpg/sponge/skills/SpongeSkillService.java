

package cz.neumimto.rpg.sponge.skills;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.skills.AbstractSkillService;
import cz.neumimto.rpg.sponge.gui.SkillTreeInterfaceModel;
import cz.neumimto.rpg.sponge.skills.types.TargetedScriptSkill;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 1.1.2015.
 */
@Singleton
public class SpongeSkillService extends AbstractSkillService {

    private Map<Character, SkillTreeInterfaceModel> guiModelByCharacter;

    private Map<Short, SkillTreeInterfaceModel> guiModelById;

    public SpongeSkillService() {
        guiModelByCharacter = new HashMap<>();
        guiModelById = new HashMap<>();
    }

    @Override
    public void load() {
        int i = 0;

        for (String str : Rpg.get().getPluginConfig().SKILLTREE_RELATIONS) {
            String[] split = str.split(",");

            short k = (short) (Short.MAX_VALUE - i);
            SkillTreeInterfaceModel model = new SkillTreeInterfaceModel(Integer.parseInt(split[3]),
                    Sponge.getRegistry().getType(ItemType.class, split[1]).orElse(ItemTypes.STICK),
                    split[2], k);

            guiModelById.put(k, model);
            guiModelByCharacter.put(split[0].charAt(0), model);
            i++;
        }

        super.load();
    }

    @Override
    public ISkillTreeInterfaceModel getGuiModelByCharacter(char c) {
        return guiModelByCharacter.get(c);
    }


    public SkillTreeInterfaceModel getGuiModelById(Short k) {
        return guiModelById.get(k);
    }

}
