package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.SkillMechanic;
import cz.neumimto.rpg.common.skills.scripting.Target;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SkillMechanic("apply_effect")
public class ApplyEffect {

    @Inject
    private EffectService effectService;

    @Handler
    public void apply(@Target IEntity entity, IEffect effect, ISkill source) {
    //    effectService.addEffect(effect, entity, source);
    }

}
