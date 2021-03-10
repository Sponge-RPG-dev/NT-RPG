package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;

import javax.inject.Inject;

public abstract class ToggleableSkill<T extends IActiveCharacter> extends ActiveSkill<T> {

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
