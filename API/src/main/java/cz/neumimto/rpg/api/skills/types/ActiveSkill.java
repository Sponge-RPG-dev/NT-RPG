

package cz.neumimto.rpg.api.skills.types;

import com.google.inject.Inject;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillExecutionType;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.skills.tree.SkillType;

/**
 * Created by NeumimTo on 26.7.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public abstract class ActiveSkill<T extends IActiveCharacter> extends AbstractSkill<T> implements IActiveSkill<T> {

    @Inject
    private InventoryService inventoryService;

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.COOLDOWN, 10000);
        settings.addNode(SkillNodes.HPCOST, 0);
        settings.addNode(SkillNodes.MANACOST, 0);
    }

    @Override
    public SkillResult onPreUse(T character, PlayerSkillContext esi) {

        if (character.isSilenced() && !getSkillTypes().contains(SkillType.CAN_CAST_WHILE_SILENCED)) {
            String translate = localizationService.translate(LocalizationKeys.PLAYER_SILENCED);
            character.sendMessage(translate);
            return SkillResult.CASTER_SILENCED;
        }

        return cast(character, esi);
    }

    public abstract SkillResult cast(T character, PlayerSkillContext info);

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.ACTIVE;
    }
}
