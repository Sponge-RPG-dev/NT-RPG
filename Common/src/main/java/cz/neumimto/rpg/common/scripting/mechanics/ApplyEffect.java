package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.skills.ISkill;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ApplyEffect {

    @Inject
    private EffectService effectService;

    public void apply(IEffect effect, ISkill source) {
        effectService.addEffect(effect, source);
    }

}
