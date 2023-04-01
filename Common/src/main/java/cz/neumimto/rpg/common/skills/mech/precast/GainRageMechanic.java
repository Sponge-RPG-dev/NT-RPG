package cz.neumimto.rpg.common.skills.mech.precast;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.mech.ValidatedMechanic;
import cz.neumimto.rpg.common.skills.reagents.ISkillCastMechanic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkillCastMechanic.class)
public class GainRageMechanic implements ISkillCastMechanic, ValidatedMechanic {

    @Inject
    private CharacterService characterService;

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return isValid(skillData, "gain_rage");
    }

    @Override
    public void processAfterSuccess(ActiveCharacter character, PlayerSkillContext context) {
        double f = context.getFloatNodeValue("gain_rage");
        characterService.gainResource(character, f, context.getSkill(), ResourceService.rage);
    }
}
