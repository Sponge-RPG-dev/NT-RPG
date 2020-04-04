package cz.neumimto.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.Resourcepack;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.TargetedEntitySkill;
import cz.neumimto.rpg.spigot.skills.scripting.For_Each_Nearby_Enemy;
import cz.neumimto.rpg.spigot.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK;

@Singleton
@ResourceLoader.Skill("ntrpg:battlecharge")
public class BattleCharge extends TargetedEntitySkill {

    @Inject
    private SpigotDamageService damageService;

    private void fillCircle(Vector[] d, double radius) {
        double increment = (2 * Math.PI ) / d.length;
        for (int i = 0; i < d.length; i++) {
            double angle = i * increment;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            d[i] = new Vector(x, 0, z);
        }
    }

    Vector[] firstTick;
    Vector[] secondTick;

    @Override
    public void init() {
        super.init();

        setDamageType(ENTITY_ATTACK.name());
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        settings.addNode(SkillNodes.RANGE, 10, 1);
        settings.addNode("slow-duration-ticks", 10000, 150);
        settings.addNode(SkillNodes.RADIUS, 3, 0);
        addSkillType(SkillType.MOVEMENT);
        addSkillType(SkillType.CANNOT_BE_SELF_CASTED);

        firstTick = new Vector[5];
        fillCircle(firstTick, 0.5);

        secondTick = new Vector[10];
        fillCircle(secondTick, 1.3);
    }

    @Override
    public void castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info, SkillContext skillContext) {
        Player entity = source.getEntity();
        LivingEntity entity1 = (LivingEntity) target.getEntity();
        Location b = entity.getLocation();
        Location a = entity1.getLocation();
        Vector vector1 = MathUtils.calculateVelocityForParabolicMotion(b.toVector(), a.toVector(), 0.5);
        entity.setVelocity(vector1);

        double r = skillContext.getDoubleNodeValue(SkillNodes.RADIUS);
        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        int slowDuration = skillContext.getIntNodeValue("slow-duration-tick");


        new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead() || entity.isSwimming() || entity.isGliding()) {
                    cancel();
                    return;
                }
                if (entity.isOnGround()) {
                    cancel();

                    Collection<Entity> nearbyEntities = entity.getNearbyEntities(r,r,r);
                    for (Entity nearbyEntity : nearbyEntities) {
                        if (nearbyEntity instanceof LivingEntity) {
                            LivingEntity living = (LivingEntity) nearbyEntity;
                            if (damageService.canDamage(source, living)) {
                                if (damage > 0) {
                                    damageService.damage(entity, living, ENTITY_ATTACK, damage, false);
                                }
                                if (slowDuration > 0) {
                                    PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, slowDuration, 2);
                                    living.addPotionEffect(pe, true);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(SpigotRpgPlugin.getInstance(), 10L, 2L);
    }
}
