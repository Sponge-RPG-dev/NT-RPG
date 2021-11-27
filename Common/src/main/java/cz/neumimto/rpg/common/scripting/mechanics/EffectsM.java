package cz.neumimto.rpg.common.scripting.mechanics;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.effects.IEffectSourceProvider;
import cz.neumimto.rpg.common.entity.IEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

import static cz.neumimto.nts.annotations.ScriptMeta.*;

@Singleton
@AutoService(NTScriptProxy.class)
public class EffectsM implements NTScriptProxy {

    @Inject
    private EffectService effectService;

    @Function("add_effect")
    @Handler
    public void applyEffect(
            @NamedParam("e|effect") IEffect effect,
            @NamedParam("s|source") IEffectSourceProvider provider,
            @NamedParam("es|entity_source") IEntity entity
    ) {
        effectService.addEffect(effect, provider, entity);
    }

    @Function("remove_effect")
    @Handler
    public void removeEffect(@NamedParam("en|effect_name") String effect,
                             @NamedParam("e|entity") IEntity target) {
        if (target.hasEffect(effect)) {
            effectService.removeEffectContainer(target.getEffect(effect), target);
        }
    }

    @Function("get_effect")
    @Handler
    public IEffectContainer getEffect(@NamedParam("en|effect_name") String effect,
                                      @NamedParam("e|entity") IEntity target) {
        return target.getEffect(effect);
    }

}
