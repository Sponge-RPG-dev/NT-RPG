package cz.neumimto.rpg.common.skills.reagents;

import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.CommonProperties;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HPCast extends SkillCastMechanic {

    @Inject
    private EntityService entityService;

    @Inject
    private DamageService<IActiveCharacter, Object, IEntity<Object>> damageService;

    double getHPCost(IActiveCharacter character, PlayerSkillContext context) {
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
