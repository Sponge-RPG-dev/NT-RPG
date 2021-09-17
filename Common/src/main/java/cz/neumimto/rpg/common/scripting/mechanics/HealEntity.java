package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HealEntity {

    @Inject
    private EntityService entityService;

    public void heal(IEntity target, float amount, ISkill skill) {
        entityService.healEntity(target, amount, skill);
    }
}
