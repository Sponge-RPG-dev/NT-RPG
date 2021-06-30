package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.effects.common.StunEffect;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:bash")
public class Bash extends TargetedEntitySkill {

    @Inject
    private EffectService effectService;

    @Inject
    private SpigotDamageService damageService;

    @Inject
    private SpigotRpgPlugin plugin;

    @Override
    public void init() {
        super.init();
        setDamageType(EntityDamageEvent.DamageCause.CONTACT.name());
        addSkillType(SkillType.PHYSICAL);
        settings.addNode(SkillNodes.DAMAGE, 15);
        settings.addNode(SkillNodes.DURATION, 5000);
        settings.addNode("knockback", 0.7f);

    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext skillContext) {
        LivingEntity entity = (LivingEntity) target.getEntity();

        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        if (damage > 0) {
            damageService.damage(entity, source.getEntity(), EntityDamageEvent.DamageCause.CONTACT, damage, false);
        }

        Location location = entity.getLocation();
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1);
        location.getWorld().spawnParticle(Particle.REDSTONE, location.add(0,1,0), 8);

        double knockback = skillContext.getDoubleNodeValue("knockback");
        if (knockback > 0) {
            Location difference = location.subtract(source.getEntity().getLocation());
            Vector normalizedDifference = difference.toVector().normalize();
            Vector multiplied = normalizedDifference.multiply(knockback);
            entity.setVelocity(multiplied);

            makeTrajectory(entity);
            Bukkit.getScheduler().runTaskLater(SpigotRpgPlugin.getInstance(), () -> {
                if (!entity.isDead()) {
                    long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
                    StunEffect stunEffect = new StunEffect(target, duration);
                    effectService.addEffect(stunEffect, this);
                }
            }, 20L);
        } else {
            long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
            StunEffect stunEffect = new StunEffect(target, duration);
            effectService.addEffect(stunEffect, this);
        }
        return SkillResult.OK;
    }

    private void makeTrajectory(LivingEntity entity) {
        new BukkitRunnable() {
            private int i = 0;
            @Override
            public void run() {
                if (entity.isDead() || i == 10) {
                    cancel();
                } else {
                    entity.getWorld().spawnParticle(Particle.SPELL_MOB, entity.getLocation().add(0,.5,0),
                            3,238, 63, 55);
                    i++;
                }
            }
        }.runTaskTimer(SpigotRpgPlugin.getInstance(), 0, 2);
    }

}
