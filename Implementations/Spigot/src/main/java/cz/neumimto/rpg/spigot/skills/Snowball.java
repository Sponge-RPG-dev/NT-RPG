package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.ProjectileCache;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Singleton;

@Singleton
@ResourceLoader.Skill("ntrpg:Snowball")
public class Snowball extends ActiveSkill<ISpigotCharacter> {

    @Override
    public void init() {
        super.init();
        setDamageType(EntityDamageEvent.DamageCause.MAGIC.name());
        settings.addExpression(SkillNodes.DAMAGE, "5 + level");
        settings.addExpression(SkillNodes.VELOCITY, "1.1");
        addSkillType(SkillType.SUMMON);
        addSkillType(SkillType.PROJECTILE);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.ICE);
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player p = character.getPlayer();
        World world = p.getWorld();

        org.bukkit.entity.Snowball snowball = (org.bukkit.entity.Snowball) world.spawnEntity(p.getLocation().clone().add(0, 1, 0).add(p.getLocation().getDirection()), EntityType.SNOWBALL);
        snowball.setVelocity(p.getLocation().getDirection().multiply(skillContext.getFloatNodeValue(SkillNodes.VELOCITY)));
        snowball.setShooter(p);
        snowball.setBounce(true);

        Trail trail = new Trail(snowball);
        trail.runTaskTimer(SpigotRpgPlugin.getInstance(), 0, 1);

        ProjectileCache projectileProperties = ProjectileCache.putAndGet(snowball, character);
        projectileProperties.setSkill(skillContext);
        projectileProperties.onHit((event, attacker, target) -> {
            event.setDamage(skillContext.getDoubleNodeValue(SkillNodes.DAMAGE));
            LivingEntity entity = target.getEntity();
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1, true, true, true));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 80, 1, true, true, true));
            trail.cancel();
        });

        world.playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.5f, 0.5f);
        return SkillResult.OK;
    }

    static class Trail extends BukkitRunnable {
        private Projectile projectile;
        private int max = 50;
        public Trail(Projectile snowball) {
            this.projectile = snowball;
        }

        @Override
        public void run() {
            max --;
            projectile.getWorld().spawnParticle(Particle.SNOWBALL, projectile.getLocation(), 2);
            projectile.getWorld().spawnParticle(Particle.BLOCK_CRACK, projectile.getLocation(), 4, 0.0, 0.1, 0.0, Material.FROSTED_ICE);
            if (max == 0) {
                cancel();
            }
        }
    }

}
