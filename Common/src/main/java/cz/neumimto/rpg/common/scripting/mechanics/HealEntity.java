package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.skills.scripting.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SkillMechanic("heal_entity")
public class HealEntity {

    @Inject
    private EntityService entityService;

    @Handler
    public void removeEffect(@Target IEntity target, @SkillArgument("healed_amount") float amount, ISkill skill) {
        entityService.healEntity(target, amount, skill);
    }
}
