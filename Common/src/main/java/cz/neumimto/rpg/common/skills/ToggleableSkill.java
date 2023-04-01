package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;

import javax.inject.Inject;

public abstract class ToggleableSkill<T extends ActiveCharacter> extends ActiveSkill<T> {

    @Inject
    private EffectService effectService;

    @Override
    public SkillResult cast(T character, PlayerSkillContext info) {
        if (character.hasEffect(getEffectName())) {
            character.removeEffect(getEffectName());
            return SkillResult.OK;
        } else {
            effectService.addEffect(constructEffect(character, info));
            return SkillResult.OK_NO_COOLDOWN;
        }
    }

    public abstract String getEffectName();

    public abstract IEffect constructEffect(T character, PlayerSkillContext info);
}
