package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.SkillArgument;
import cz.neumimto.rpg.common.skills.scripting.SkillMechanic;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SkillMechanic("apply_effect")
public class ApplyEffect {

    @Inject
    private EffectService effectService;

    @Handler
    public void apply(@SkillArgument("effect") IEffect effect, ISkill source) {
        effectService.addEffect(effect, source);
    }

}
