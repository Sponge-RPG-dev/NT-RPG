package cz.neumimto.rpg.common.skills.reagents;

import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Cooldown extends SkillCostPipelineMechanic {

    @Inject
    private EntityService entityService;

    private long getCooldown(IActiveCharacter character, PlayerSkillContext context) {
        return (long) (context.getFloatNodeValue(SkillNodes.COOLDOWN)
                * entityService.getEntityProperty(character, CommonProperties.cooldown_reduce_mult));
    }

    @Override
    public SkillResult processBefore(IActiveCharacter character, PlayerSkillContext context) {
        return character.hasCooldown(context.getSkill().getId()) ? SkillResult.ON_COOLDOWN : SkillResult.OK;
    }

    @Override
    public void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context) {
        long cd = getCooldown(character, context);
        SkillData skillData = context.getSkillData();
        if (cd > 59999L) {
            character.getCharacterBase().getCharacterSkill(skillData.getSkill()).setCooldown(cd);
        }
        cd = cd + System.currentTimeMillis();
        character.getCooldowns().put(skillData.getSkill().getId(), cd);
    }

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return super.isValid(skillData, SkillNodes.COOLDOWN);
    }
}
