package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.IEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RemoveEffect {

    @Inject
    private EffectService effectService;

    public void removeEffect(String effect, IEntity target) {
        if (target.hasEffect(effect)) {
            effectService.removeEffectContainer(target.getEffect(effect), target);
        }
    }
}
