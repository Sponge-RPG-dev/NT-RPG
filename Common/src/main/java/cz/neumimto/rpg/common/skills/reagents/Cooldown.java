package cz.neumimto.rpg.common.skills.reagents;

import cz.neumimto.rpg.common.configuration.PluginConfig;
import cz.neumimto.rpg.common.entity.CommonProperties;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Cooldown extends SkillCastMechanic {

    @Inject
    private EntityService entityService;

    @Inject
    private CharacterService characterService;

    @Inject
    private PluginConfig pluginConfig;

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
        if (pluginConfig.ITEM_COOLDOWNS) {
            characterService.notifyCooldown(character, context, cd);
        }
        cd = cd + System.currentTimeMillis();
        character.getCooldowns().put(skillData.getSkill().getId(), cd);
    }

    @Override
    public boolean isValidForContext(SkillData skillData) {
        return super.isValid(skillData, SkillNodes.COOLDOWN);
    }

    @Override
    public void notifyFailure(IActiveCharacter character, PlayerSkillContext context) {
        long l = (character.getCooldown(context.getSkillData().getSkillId()) - System.currentTimeMillis()) / 1000;
        Gui.sendCooldownMessage(character, context.getSkillData().getSkillName(), l);
    }
}
