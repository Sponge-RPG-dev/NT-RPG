package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.nts.annotations.ScriptMeta.ScriptTarget;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.inventory.InventoryService;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillExecutionType;
import cz.neumimto.rpg.common.skills.SkillResult;

import javax.inject.Inject;

/**
 * Created by NeumimTo on 6.8.2015.
 */

public abstract class PassiveSkill extends AbstractSkill<ActiveCharacter> {

    public static enum Type {
        PASSIVE, UPGRADE
    }

    @Inject
    protected EffectService effectService;

    @Inject
    protected InventoryService inventoryService;

    protected String relevantEffectName;

    protected Type type = Type.PASSIVE;

    public PassiveSkill() {
    }

    public PassiveSkill(String name) {
        this.relevantEffectName = name;
    }

    @Override
    public SkillResult onPreUse(ActiveCharacter character, PlayerSkillContext esi) {
        PlayerSkillContext info = character.getSkillInfo(this);
        String msg = localizationService.translate(LocalizationKeys.CANT_USE_PASSIVE_SKILL,
                Arg.arg("skill", info.getSkillData().getSkillName()));
        character.sendMessage(msg);
        return SkillResult.CANCELLED;
    }

    protected void update(ActiveCharacter ActiveCharacter) {
        PlayerSkillContext skill = ActiveCharacter.getSkill(getId());
        effectService.removeEffect(relevantEffectName, ActiveCharacter, this);
        applyEffect(skill, ActiveCharacter);
    }

    @Override
    public void onCharacterInit(ActiveCharacter c, int level, PlayerSkillContext context) {
        super.onCharacterInit(c, level, context);
        update(c);
    }

    @Override
    public void skillLearn(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(ActiveCharacter, context);
        update(ActiveCharacter);
    }

    @Override
    public void skillRefund(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(ActiveCharacter, context);
        PlayerSkillContext skillInfo = ActiveCharacter.getSkillInfo(this);
        if (skillInfo.getLevel() <= 0) {
            effectService.removeEffect(relevantEffectName, ActiveCharacter, this);
        } else {
            update(ActiveCharacter);
        }
    }

    @ScriptTarget
    public abstract void applyEffect(@NamedParam("c|context") PlayerSkillContext info,
                                     @NamedParam("caster") ActiveCharacter character);

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.PASSIVE;
    }

    public String getRelevantEffectName() {
        return relevantEffectName;
    }

    public void setRelevantEffectName(String relevantEffectName) {
        this.relevantEffectName = relevantEffectName;
    }
}
