package cz.neumimto.rpg.common.skills.mech.precast;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.mech.ValidatedMechanic;
import cz.neumimto.rpg.common.skills.reagents.ISkillCastMechanic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkillCastMechanic.class)
public class GainHpMechanic implements ISkillCastMechanic, ValidatedMechanic {

    @Inject
    private EntityService entityService;

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return isValid(skillData, "gain_hp");
    }

    @Override
    public void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context) {
        double f = context.getFloatNodeValue("gain_hp");
        entityService.healEntity(character, f, context.getSkill());
    }

}

