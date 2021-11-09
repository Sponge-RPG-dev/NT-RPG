package cz.neumimto.rpg.common.skills.reagents;

import cz.neumimto.rpg.common.entity.CommonProperties;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ManaCast extends SkillCastMechanic {

    @Inject
    private EntityService entityService;

    private double getManaRequired(IActiveCharacter character, PlayerSkillContext context) {
        return context.getFloatNodeValue(SkillNodes.MANACOST)
                * entityService.getEntityProperty(character, CommonProperties.mana_cost_reduce);
    }

    @Override
    public SkillResult processBefore(IActiveCharacter character, PlayerSkillContext context) {
        if (character.getMana().getValue() < getManaRequired(character, context)) {
            return SkillResult.NO_MANA;
        }
        return SkillResult.OK;
    }

    @Override
    public void processAfterSuccess(IActiveCharacter character, PlayerSkillContext context) {
        character.getMana().setValue(character.getMana().getValue() - getManaRequired(character, context));
        Gui.displayMana(character);
    }

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return isValid(skillData, SkillNodes.MANACOST);
    }

}
