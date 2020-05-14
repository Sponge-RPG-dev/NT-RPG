package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.common.skills.reagents.ISkillCastMechanic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GainManaMechanic implements ISkillCastMechanic, ValidatedMechanic {

    @Inject
    private CharacterService characterService;

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return isValid(skillData, "gain_mana");
    }

    @Override
    public void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context) {
        float f = context.getFloatNodeValue("gain_mana");
        characterService.gainMana(character, f, context.getSkill());
    }
}
