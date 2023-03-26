package cz.neumimto.rpg.spigot.damage;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.CommonProperties;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import javax.inject.Singleton;
import java.util.Map;
import java.util.TreeMap;

@Singleton
public class SpigotDamageService extends DamageService<ISpigotCharacter, LivingEntity, ISpigotEntity<LivingEntity>> {

    private Map<Double, String> doubleColorMap = new TreeMap<>();

    private String[] colorScale = new String[]{
            "§f",
            "§e",
            "§6",
            "§c",
            "§4",
            "§5",
            "§1"
    };


    public SpigotDamageService() {
        setDamageHandler(new SpigotDamageHandler());
    }

    @Override
    public void damageEntity(ISpigotEntity<LivingEntity> entity, double value) {
        entity.getEntity().damage(value);
        //todo workaround bukkit stupidity entity.setLastDamageCause
    }

    public boolean canDamage(ISpigotEntity damager, LivingEntity damaged) {
        LivingEntity entity = damager.getEntity();
        if (entity.getType() == EntityType.PLAYER) {
            if (damaged instanceof Tameable t) {
                if (t.getOwner() != null && t.getOwner().equals(entity)) {
                    return false;
                }
            }
            return super.canDamage((ISpigotCharacter) damaged, damaged);
        } else {
            if (damaged instanceof Tameable t) {
                if (t.getOwner() != null && t.getOwner().equals(entity)) {
                    return false;
                }
            }
            //todo
            return true;
        }
    }

    @Override
    public void init() {
    }

    public DamageCause damageTypeById(String damageType) {
        return DamageCause.valueOf(damageType);
    }

    public boolean damage(LivingEntity attacker, LivingEntity target, DamageCause cause, double damage, boolean knockback) {
        if (target.isDead() || target.getHealth() <= 0.0) {
            return false;
        }
        target.damage(damage, attacker);

        return true;
    }

    public boolean damage(LivingEntity target, DamageCause cause, double damage, boolean knockback) {
        if (target.isDead() || target.getHealth() <= 0.0) {
            return false;
        }
        target.damage(damage);

        return true;
    }

    public static class SpigotDamageHandler extends DamageHandler<ISpigotCharacter, LivingEntity> {

        @Override
        public boolean canDamage(ISpigotCharacter damager, LivingEntity l) {
            if (damager.getEntity() == l || l.getHealth() <= 0 || l.isDead() || l.isInvulnerable()) {
                return false;
            }
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager.getEntity(), l, DamageCause.CUSTOM, 0);
            //TODO Maybe fire this event?
            return !event.isCancelled();
        }

        @Override
        public double getEntityResistance(IEntity entity, String damageType) {
            EntityService entityService = Rpg.get().getEntityService();
            DamageCause source = DamageCause.valueOf(damageType);
            //TODO?
            return 1;
        }

        @Override
        public double getEntityDamageMult(IEntity entity, String damageType) {
            EntityService entityService = Rpg.get().getEntityService();
            DamageCause source = DamageCause.valueOf(damageType);
            if (source == DamageCause.ENTITY_ATTACK) {
                return entityService.getEntityProperty(entity, CommonProperties.physical_damage_bonus_mult);
            }
            if (source == DamageCause.MAGIC) {
                return entityService.getEntityProperty(entity, CommonProperties.magic_damage_bonus_mult);
            }
            if (source == DamageCause.FIRE) {
                return entityService.getEntityProperty(entity, CommonProperties.fire_damage_bonus_mult);
            }
            if (source == DamageCause.LIGHTNING) {
                return entityService.getEntityProperty(entity, CommonProperties.lightning_damage_bonus_mult);
            }
            return 1;
        }

    }

}
