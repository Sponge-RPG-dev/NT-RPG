package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DamageMechanic {

    @Inject
    private DamageService damageService;

    public void damage(ActiveCharacter character, IEntity entity, float damage) {
        if (damageService.canDamage(character, entity.getEntity())) {
            damageService.damageEntity(entity, damage);
        }
    }
}
