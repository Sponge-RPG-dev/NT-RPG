package cz.neumimto.skills.listeners;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.skills.effects.positive.NoAutohealEffect;
import cz.neumimto.skills.effects.positive.UnhealEffect;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ResourceLoader.ListenerClass
public class SkillpackListener implements Listener {

    @Inject
    private SpigotEntityService spigotEntityService;

    @Inject
    private SpigotDamageService spigotDamageService;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityHeal(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();
        LivingEntity livingEntity = (LivingEntity) entity;
        IEntity iEntity = spigotEntityService.get(livingEntity);



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
            livingEntity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, livingEntity.getLocation(), 5, 2,2,2);
        }
    }
}
