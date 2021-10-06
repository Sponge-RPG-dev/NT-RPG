package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;

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
}
