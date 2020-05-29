package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.reagents.ISkillCastMechanic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GainHpMechanic implements ISkillCastMechanic, ValidatedMechanic {

    @Inject
    private EntityService entityService;

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return isValid(skillData, "gain_hp");
    }

    @Override
    public void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context) {
        float f = context.getFloatNodeValue("gain_hp");
        entityService.healEntity(character, f, context.getSkill());
    }

}

