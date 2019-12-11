package cz.neumimto.rpg.spigot.listeners;

import com.google.inject.Singleton;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IEntityType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.events.damage.IEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.damage.AbstractDamageListener;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.ProjectileCache;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntityProjectileDamageEarlyEvent;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntitySkillDamageEarlyEvent;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntityWeaponDamageEarlyEvent;
import cz.neumimto.rpg.spigot.inventory.SpigotItemService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import javax.inject.Inject;
import java.util.Optional;

@Singleton
@ResourceLoader.ListenerClass
public class SpigotDamageListener extends AbstractDamageListener implements Listener {

    @Inject
    private SpigotEntityService entityService;

    @Inject
    private SpigotDamageService spigotDamageService;

    @Inject
    private SpigotItemService itemService;

    @Inject
    private PluginConfig pluginConfig;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageEarly(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        LivingEntity living = (LivingEntity) entity;
        ISpigotEntity target = (ISpigotEntity) entityService.get(living);

        IEntity attacker = null;
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Projectile projectile = (Projectile) damager;
            ProjectileSource shooter = projectile.getShooter();

            if (shooter instanceof LivingEntity) {
                attacker = entityService.get((LivingEntity) shooter);

                ProjectileCache projectileProperties = ProjectileCache.cache.get(projectile);
                if (projectileProperties != null) {
                    event.setCancelled(true);
                    ProjectileCache.cache.remove(projectile);
                    projectileProperties.consumer.accept(event, attacker, target);
                    return;
                }
                processProjectileDamageEarly(event, attacker, target, projectile);
            }

        } else {
            attacker = entityService.get((LivingEntity) damager);
        }

        if (target.skillOrEffectDamageCayse() != null) {
            processSkillDamageEarly(event, target.skillOrEffectDamageCayse(), attacker, target);
        } else {
            processWeaponDamageEarly(event, event.getCause(), attacker, target);
        }


    }

    private void processWeaponDamageEarly(EntityDamageByEntityEvent event, EntityDamageEvent.DamageCause cause, IEntity attacker, ISpigotEntity target) {
        double newdamage = event.getDamage();

        RpgItemStack rpgItemStack = null;
        if (attacker.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) attacker;
            if (character.requiresDamageRecalculation()) {
                RpgItemStack mainHand = character.getMainHand();
                spigotDamageService.recalculateCharacterWeaponDamage(character, mainHand);
                character.setRequiresDamageRecalculation(false);
            }
            newdamage = character.getWeaponDamage();
            rpgItemStack = character.getMainHand();
        } else {
            LivingEntity entity = (LivingEntity) attacker.getEntity();
            if (!pluginConfig.OVERRIDE_MOBS) {
                newdamage = entityService.getMobDamage(entity.getWorld().getName(), entity.getType().name());
            }
            if (entity instanceof HumanEntity) {
                ItemStack itemStack = ((HumanEntity) entity).getItemInHand();

                Optional<RpgItemStack> rpgItemStack1 = itemService.getRpgItemStack(itemStack);
                if (rpgItemStack1.isPresent()) {
                    rpgItemStack = rpgItemStack1.get();
                }

            }
        }
        newdamage *= spigotDamageService.getEntityDamageMult(attacker, event.getCause());

        IEntityWeaponDamageEarlyEvent e = getWeaponDamage(event, target, newdamage, rpgItemStack, SpigotEntityWeaponDamageEarlyEvent.class);
        event.setDamage(e.getDamage());
    }

    protected IEntityWeaponDamageEarlyEvent getWeaponDamage(EntityDamageByEntityEvent event, IEntity target, double newdamage, RpgItemStack rpgItemStack, Class<? extends IEntityWeaponDamageEarlyEvent> impl) {
        IEntityWeaponDamageEarlyEvent e = Rpg.get().getEventFactory().createEventInstance(impl);
        e.setTarget(target);
        e.setDamage(newdamage);
        e.setWeapon(rpgItemStack);

        if (Rpg.get().postEvent(e)) {
            event.setCancelled(true);
            return null;
        }

        if (e.getDamage() <= 0) {
            return null;
        }
        return e;
    }


    private void processSkillDamageEarly(EntityDamageByEntityEvent event, ISkill skill, IEntity attacker, ISpigotEntity target) {

        EntityDamageEvent.DamageCause type = event.getCause();

        if (attacker.getType() == IEntityType.CHARACTER) {
            SpigotCharacter c = (SpigotCharacter) attacker;
            if (c.hasPreferedDamageType()) {
                type = spigotDamageService.damageTypeById(c.getDamageType());
            }
        }
        double newdamage = event.getDamage() * spigotDamageService.getEntityDamageMult(attacker, type);

        SpigotEntitySkillDamageEarlyEvent e = Rpg.get().getEventFactory().createEventInstance(SpigotEntitySkillDamageEarlyEvent.class);
        e.setTarget(target);
        e.setDamage(newdamage);
        e.setSkill(skill);

        if (Rpg.get().postEvent(e)) {
            event.setCancelled(true);
            return;
        }

        if (e.getDamage() <= 0) {
            return;
        }

        event.setDamage(e.getDamage());

    }

    private void processProjectileDamageEarly(EntityDamageByEntityEvent event,  IEntity attacker, IEntity target, Projectile projectile) {
        double newdamage = event.getDamage();
        if (attacker.getType() == IEntityType.CHARACTER) {
            IActiveCharacter c = (IActiveCharacter) attacker;
            newdamage = spigotDamageService.getCharacterProjectileDamage(c, projectile.getType());
        } else if (attacker.getType() == IEntityType.MOB) {
            PluginConfig pluginConfig = Rpg.get().getPluginConfig();
            if (!pluginConfig.OVERRIDE_MOBS) {
                newdamage = entityService.getMobDamage((LivingEntity) attacker.getEntity());
            }
        }

        SpigotEntityProjectileDamageEarlyEvent e = Rpg.get().getEventFactory().createEventInstance(SpigotEntityProjectileDamageEarlyEvent.class);
        e.setTarget(target);
        e.setDamage(newdamage);
        e.setProjectile(projectile);


        if (Rpg.get().postEvent(e)) {
            e.setCancelled(true);
            return;
        }

        if (e.getDamage() <= 0) {
            return;
        }

        event.setDamage(e.getDamage());
    }
}
