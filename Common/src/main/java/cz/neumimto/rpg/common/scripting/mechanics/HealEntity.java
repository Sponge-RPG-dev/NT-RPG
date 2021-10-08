package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.skills.ISkill;

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
