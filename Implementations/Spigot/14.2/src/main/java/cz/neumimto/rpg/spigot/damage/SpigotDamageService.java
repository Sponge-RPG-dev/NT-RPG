package cz.neumimto.rpg.spigot.damage;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.common.damage.AbstractDamageService;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.inject.Singleton;

@Singleton
public class SpigotDamageService extends AbstractDamageService<LivingEntity> {


    @Override
    public void damageEntity(IEntity<LivingEntity> character, double maxValue) {
        character.getEntity().damage(maxValue);
    }

    @Override
    public void init() {

    }


    public void double getEntityDamageMult(IEntity attacker, EntityDamageEvent.DamageCause type) {

    }
}
