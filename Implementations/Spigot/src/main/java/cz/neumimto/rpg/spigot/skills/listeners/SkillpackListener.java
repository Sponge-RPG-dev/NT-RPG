package cz.neumimto.rpg.spigot.skills.listeners;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.effects.common.*;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.services.IRpgListener;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(IRpgListener.class)
public class SkillpackListener implements IRpgListener {

    @Inject
    private SpigotEntityService spigotEntityService;

    @Inject
    private SpigotDamageService spigotDamageService;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityHeal(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        LivingEntity livingEntity = (LivingEntity) entity;
        IEntity iEntity = spigotEntityService.get(livingEntity);

        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.CUSTOM) {
            if (iEntity.hasEffect(NoNaturalHealingEffect.name)) {
                event.setAmount(0);
                event.setCancelled(true);
                return;
            }
        }

        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) {
            if (iEntity.hasEffect(NoAutohealEffect.name)) {
                event.setCancelled(true);
                event.setAmount(0);
            }
        }

        IEffectContainer effect = iEntity.getEffect(UnhealEffect.name);
        if (effect != null) {
            Float mult = (Float) effect.getStackedValue();
            spigotDamageService.damage(livingEntity, EntityDamageEvent.DamageCause.MAGIC, event.getAmount() * mult, false);
            livingEntity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, livingEntity.getLocation(), 5, 2, 2, 2);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            LivingEntity l = (LivingEntity) entity;
            IEntity iEntity = spigotEntityService.get(l);

            if (iEntity.hasEffect(FeatherFall.name) && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }
            if (PiggifyEffect.entities.contains(entity.getUniqueId())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDismountVehicle(EntityDismountEvent event) {
        if (event instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) event;
            IEntity iEntity = spigotEntityService.get(le);
            if (iEntity.hasEffect(PiggifyEffect.name)) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
