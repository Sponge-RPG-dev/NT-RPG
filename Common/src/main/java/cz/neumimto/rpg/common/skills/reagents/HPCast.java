package cz.neumimto.rpg.common.skills.reagents;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.CommonProperties;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkillCastMechanic.class)
public class HPCast extends SkillCastMechanic {

    @Inject
    private EntityService entityService;

    @Inject
    private DamageService damageService;

    double getHPCost(ActiveCharacter character, PlayerSkillContext context) {
        return context.getFloatNodeValue(SkillNodes.HPCOST)
                * entityService.getEntityProperty(character, CommonProperties.health_cost_reduce);
    }

    @Override
    public SkillResult processBefore(ActiveCharacter character, PlayerSkillContext context) {
        Resource health = character.getResource(ResourceService.health);
        if (health.getValue() < getHPCost(character, context)) {
            return SkillResult.NO_HP;
        }
        return SkillResult.OK;
    }

    @Override
    public void processAfterSuccess(ActiveCharacter character, PlayerSkillContext context) {
        Resource health = character.getResource(ResourceService.health);
        double newHp = health.getValue() - getHPCost(character, context);
        if (newHp <= 0) {
            kill(character);
        } else {
            health.setValue(newHp);
        }
    }

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return super.isValid(skillData, SkillNodes.HPCOST);
    }

    void kill(ActiveCharacter character) {
        damageService.damageEntity(character, Double.MAX_VALUE);
    }
}
