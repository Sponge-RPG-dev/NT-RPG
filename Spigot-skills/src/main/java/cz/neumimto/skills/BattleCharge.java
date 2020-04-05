package cz.neumimto.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.skills.TargetedEntitySkill;
import cz.neumimto.rpg.spigot.utils.MathUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK;

@Singleton
@ResourceLoader.Skill("ntrpg:battlecharge")
public class BattleCharge extends TargetedEntitySkill {

    @Inject
    private SpigotDamageService damageService;

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

        skillContext.next(source, info, skillContext.result(SkillResult.OK));
    }
}
