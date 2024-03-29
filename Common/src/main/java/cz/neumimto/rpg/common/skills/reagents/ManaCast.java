package cz.neumimto.rpg.common.skills.reagents;

import com.google.auto.service.AutoService;
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
public class ManaCast extends SkillCastMechanic {

    @Inject
    private EntityService entityService;

    private double getManaRequired(ActiveCharacter character, PlayerSkillContext context) {
        return context.getFloatNodeValue(SkillNodes.MANACOST)
                * entityService.getEntityProperty(character, CommonProperties.mana_cost_reduce);
    }

    @Override
    public SkillResult processBefore(ActiveCharacter character, PlayerSkillContext context) {
        Resource mana = character.getResource(ResourceService.mana);
        if (mana == null || mana.getValue() < getManaRequired(character, context)) {
            return SkillResult.NO_MANA;
        }
        return SkillResult.OK;
    }

    @Override
    public void processAfterSuccess(ActiveCharacter character, PlayerSkillContext context) {
        Resource mana = character.getResource(ResourceService.mana);
        mana.setValue(mana.getValue() - getManaRequired(character, context));
        character.updateResourceUIHandler();
    }

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return isValid(skillData, SkillNodes.MANACOST);
    }

}
