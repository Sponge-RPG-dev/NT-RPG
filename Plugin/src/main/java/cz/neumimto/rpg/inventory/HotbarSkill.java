package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;

/**
 * Created by NeumimTo on 31.12.2015.
 */
public class HotbarSkill extends HotbarObject {
    ISkill right_skill;
    ISkill left_skill;

    public HotbarSkill() {
        type = HotbarObjectTypes.SKILL;
    }


    public ISkill getRightSkill() {
        return right_skill;
    }

    public void setRightSkill(ISkill right_skill) {
        this.right_skill = right_skill;
    }

    public ISkill getLeftSkill() {
        return left_skill;
    }

    public void setLeftSkill(ISkill left_skill) {
        this.left_skill = left_skill;
    }

    @Override
    public void onRightClick(IActiveCharacter character) {
        if (right_skill != null) {
            NtRpgPlugin.GlobalScope.skillService.executeSkill(character, right_skill);
        }
    }

    @Override
    public void onLeftClick(IActiveCharacter character) {
        if (left_skill != null) {
            NtRpgPlugin.GlobalScope.skillService.executeSkill(character, left_skill);
        }
    }

    @Override
    public IEffectSource getType() {
        return EffectSourceType.CHARM;
    }
}
