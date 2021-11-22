package cz.neumimto.rpg.common.scripting.mechanics;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.effects.IEffectSourceProvider;
import cz.neumimto.rpg.common.entity.IEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(NTScriptProxy.class)
public class EffectsM implements NTScriptProxy {

    @Inject
    private EffectService effectService;

    @ScriptMeta.Function("add_effect")
    @ScriptMeta.Handler
    public void applyEffect(
            @ScriptMeta.NamedParam("e|effect") IEffect effect,
            @ScriptMeta.NamedParam("s|source") IEffectSourceProvider provider,
            @ScriptMeta.NamedParam("es|entity_source")IEntity entity
            ) {
        effectService.addEffect(effect,provider, entity);
    }

    @ScriptMeta.Function("remove_effect")
    public void removeEffect(@ScriptMeta.NamedParam("en|effect_name") String effect, @ScriptMeta.NamedParam("e|entity") IEntity target) {
        if (target.hasEffect(effect)) {
            effectService.removeEffectContainer(target.getEffect(effect), target);
        }
    }

    @ScriptMeta.Function("get_effect")
    public IEffectContainer getEffect(@ScriptMeta.NamedParam("en|effect_name") String effect, @ScriptMeta.NamedParam("e|entity") IEntity target) {
        return target.getEffect(effect);
    }

}
