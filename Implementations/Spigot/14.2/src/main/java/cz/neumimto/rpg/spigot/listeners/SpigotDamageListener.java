package cz.neumimto.rpg.spigot.listeners;

import com.google.inject.Singleton;
import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IEntityType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.events.damage.SpigotEntitySkillDamageEarlyEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

import javax.inject.Inject;

@Singleton
@IResourceLoader.ListenerClass
public class SpigotDamageListener implements Listener {

    @Inject
    private SpigotEntityService entityService;

    @Inject
    private SpigotDamageService spigotDamageService;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageEarly(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
            return;
        }
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        LivingEntity living = (LivingEntity) entity;
        ISpigotEntity target = (ISpigotEntity) entityService.get(living);

        IEntity attacker;
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof LivingEntity) {
                attacker = entityService.get((LivingEntity) shooter);
            }
        } else {
            attacker = entityService.get((LivingEntity) damager);
        }

        if (target.skillOrEffectDamageCayse() != null) {
            processSkillDamageEarly(event, target.skillOrEffectDamageCayse(), attacker, target);
        } else {

        } else if (damageSource instanceof IndirectEntityDamageSource) {
            IndirectEntityDamageSource indirectEntityDamageSource = (IndirectEntityDamageSource) damageSource;
            Projectile projectile = (Projectile) indirectEntityDamageSource.getSource();
            ProjectileProperties projectileProperties = ProjectileProperties.cache.get(projectile);
            if (projectileProperties != null) {
                event.setCancelled(true);
                ProjectileProperties.cache.remove(projectile);
                projectileProperties.consumer.accept(event, attacker, target);
                return;
            }

            processProjectileDamageEarly(event, indirectEntityDamageSource, attacker, target, projectile);
        } else {
            processWeaponDamageEarly(event, damageSource, attacker, target);
        }


    }


    private void processSkillDamageEarly(EntityDamageByEntityEvent event, ISkill skill, IEntity attacker, ISpigotEntity target) {

        EntityDamageEvent.DamageCause type = event.getCause();

        if (attacker.getType() == IEntityType.CHARACTER) {
            IActiveCharacter c = (IActiveCharacter) attacker;
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
}
