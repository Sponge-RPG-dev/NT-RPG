package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;

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
