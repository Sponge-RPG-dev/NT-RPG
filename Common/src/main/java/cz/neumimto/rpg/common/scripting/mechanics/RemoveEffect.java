package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.SkillMechanic;
import cz.neumimto.rpg.common.skills.scripting.StaticArgument;
import cz.neumimto.rpg.common.skills.scripting.Target;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SkillMechanic("remove_effect")
public class RemoveEffect {

    @Inject
    private EffectService effectService;

    @Handler
    public void removeEffect(@StaticArgument("effect") String effect, @Target IEntity target) {
        if (target.hasEffect(effect)) {
            effectService.removeEffectContainer(target.getEffect(effect), target);
        }
    }
}
