package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.nts.annotations.ScriptMeta.ScriptTarget;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
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

public abstract class PassiveSkill extends AbstractSkill<IActiveCharacter> {

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
    public SkillResult onPreUse(IActiveCharacter character, PlayerSkillContext esi) {
        PlayerSkillContext info = character.getSkillInfo(this);
        String msg = localizationService.translate(LocalizationKeys.CANT_USE_PASSIVE_SKILL,
                Arg.arg("skill", info.getSkillData().getSkillName()));
        character.sendMessage(msg);
        return SkillResult.CANCELLED;
    }

    protected void update(IActiveCharacter IActiveCharacter) {
        PlayerSkillContext skill = IActiveCharacter.getSkill(getId());
        effectService.removeEffect(relevantEffectName, IActiveCharacter, this);
        applyEffect(skill, IActiveCharacter);
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level, PlayerSkillContext context) {
        super.onCharacterInit(c, level, context);
        update(c);
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(IActiveCharacter, context);
        update(IActiveCharacter);
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(IActiveCharacter, context);
        PlayerSkillContext skillInfo = IActiveCharacter.getSkillInfo(this);
        if (skillInfo.getLevel() <= 0) {
            effectService.removeEffect(relevantEffectName, IActiveCharacter, this);
        } else {
            update(IActiveCharacter);
        }
    }

    @ScriptTarget
    public abstract void applyEffect(@NamedParam("c|context") PlayerSkillContext info,
                                     @NamedParam("caster") IActiveCharacter character);

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
