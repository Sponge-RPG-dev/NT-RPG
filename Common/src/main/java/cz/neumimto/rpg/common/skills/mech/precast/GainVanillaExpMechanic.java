package cz.neumimto.rpg.common.skills.mech.precast;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.mech.ValidatedMechanic;
import cz.neumimto.rpg.common.skills.reagents.ISkillCastMechanic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkillCastMechanic.class)
public class GainVanillaExpMechanic implements ISkillCastMechanic, ValidatedMechanic {

    @Inject
    private CharacterService characterService;

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return isValid(skillData, "gain_exp");
    }

    @Override
    public void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context) {
        double f = context.getFloatNodeValue("gain_exp");
        characterService.addExperiences(character, f, "VANILLA");
    }
}

