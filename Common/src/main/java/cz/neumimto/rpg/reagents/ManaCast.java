package cz.neumimto.rpg.reagents;

import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ManaCast extends SkillCastMechanic {

    @Inject
    private EntityService entityService;

    private float getManaRequired(IActiveCharacter character, PlayerSkillContext context) {
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
