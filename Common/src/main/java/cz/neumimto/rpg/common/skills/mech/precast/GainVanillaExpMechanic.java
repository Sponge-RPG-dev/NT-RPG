package cz.neumimto.rpg.common.skills.mech.precast;

import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.common.skills.mech.ValidatedMechanic;
import cz.neumimto.rpg.common.skills.reagents.ISkillCastMechanic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
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

