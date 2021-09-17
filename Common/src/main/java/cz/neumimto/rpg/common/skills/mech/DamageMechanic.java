package cz.neumimto.rpg.common.skills.mech;

import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DamageMechanic {

    @Inject
    private DamageService damageService;

    public void damage(IActiveCharacter character, IEntity entity, float damage) {
        if (damageService.canDamage(character, entity.getEntity())) {
            damageService.damageEntity(entity, damage);
        }
    }
}
