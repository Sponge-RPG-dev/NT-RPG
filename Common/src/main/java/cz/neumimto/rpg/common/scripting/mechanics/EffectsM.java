package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectSourceProvider;
import cz.neumimto.rpg.common.entity.IEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EffectsM {

    @Inject
    private EffectService effectService;

    @ScriptMeta.Function("apply_effect")
    @ScriptMeta.Handler
    public void applyEffect(
            @ScriptMeta.NamedParam("effect") IEffect effect,
            @ScriptMeta.NamedParam("source") IEffectSourceProvider provider,
            @ScriptMeta.NamedParam("entity_source")IEntity entity
            ) {
        effectService.addEffect(effect,provider, entity);
    }

    @ScriptMeta.Function("remote_effect")
    public void removeEffect(@ScriptMeta.NamedParam("ef|effect_name") String effect, @ScriptMeta.NamedParam("e|entity") IEntity target) {
        if (target.hasEffect(effect)) {
            effectService.removeEffectContainer(target.getEffect(effect), target);
        }
    }

}
