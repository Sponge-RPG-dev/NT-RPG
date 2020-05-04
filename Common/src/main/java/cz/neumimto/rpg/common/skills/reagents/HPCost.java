package cz.neumimto.rpg.common.skills.reagents;

import cz.neumimto.rpg.api.damage.DamageService;
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
public class HPCost extends SkillCostPipelineMechanic {

    @Inject
    private EntityService entityService;

    @Inject
    private DamageService damageService;

    float getHPCost(IActiveCharacter character, PlayerSkillContext context) {
        return context.getFloatNodeValue(SkillNodes.HPCOST)
                * entityService.getEntityProperty(character, CommonProperties.health_cost_reduce);
    }

    @Override
    public SkillResult processBefore(IActiveCharacter character, PlayerSkillContext context) {
        if (character.getHealth().getValue() < getHPCost(character, context)) {
            return SkillResult.NO_HP;
        }
        return SkillResult.OK;
    }

    @Override
    public void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context) {
        double newHp = character.getHealth().getValue() - getHPCost(character, context);
        if (newHp <= 0) {
            kill(character);
        } else {
            character.getHealth().setValue(newHp);
        }
    }

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return super.isValid(skillData, SkillNodes.HPCOST);
    }

    void kill(IActiveCharacter character) {
        damageService.damageEntity(character, Double.MAX_VALUE);
    }
}
