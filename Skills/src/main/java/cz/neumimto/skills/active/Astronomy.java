package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.Utils;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ActiveSkill;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillModifier;
import cz.neumimto.rpg.skills.SkillResult;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 3.8.2017.
 */
@ResourceLoader.Skill
public class Astronomy extends ActiveSkill {

    public Astronomy() {
        setName(SkillLocalization.ASTRONOMY_NAME);
        setDescription(SkillLocalization.ASTRONOMY_DESC);
        setSettings(settings);
    }

    @Override
    public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {

        ItemStack is = Utils.createTeleportationScroll(character.getLocation());
        character.getEntity().getInventory().offer(is);
        return SkillResult.OK;
    }
}
