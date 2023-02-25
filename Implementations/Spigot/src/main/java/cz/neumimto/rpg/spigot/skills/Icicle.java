package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.ProjectileCache;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:icicle")
public class Icicle extends ActiveSkill<ISpigotCharacter> {

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

        org.bukkit.entity.Snowball snowball = (org.bukkit.entity.Snowball) world.spawnEntity(p.getEyeLocation().clone().add(p.getLocation().getDirection()), EntityType.SNOWBALL);
        snowball.setVelocity(p.getLocation().getDirection().multiply(skillContext.getFloatNodeValue(SkillNodes.VELOCITY)));
        snowball.setShooter(p);


        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(12349);
        itemStack.setItemMeta(itemMeta);
        snowball.setItem(itemStack);


        Trail trail = new Trail(snowball);
        trail.runTaskTimer(SpigotRpgPlugin.getInstance(), 0, 1);

        ProjectileCache projectileProperties = ProjectileCache.putAndGet(snowball, character);
        projectileProperties.setSkill(skillContext);
        projectileProperties.onHit((event, attacker, target) -> {
            event.setDamage(skillContext.getDoubleNodeValue(SkillNodes.DAMAGE));
            LivingEntity entity = target.getEntity();
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1, true, true, true));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 80, 1, true, true, true));
            entity.setFireTicks(0);
            trail.cancel();
        });
        projectileProperties.onHitBlock(block -> {
            Block relative = block.getRelative(BlockFace.UP);
            Location location = relative.getLocation();
            if (relative.getType() == Material.AIR) {
                for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                    onlinePlayer.sendBlockChange(location, Material.SNOW.createBlockData());
                }
            }
            trail.cancel();
        });
        world.playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.5f, 0.5f);
        return SkillResult.OK;
    }

    static class Trail extends BukkitRunnable {
        private Projectile projectile;
        private int max = 500;
        BlockData data = null;

        public Trail(Projectile snowball) {
            this.projectile = snowball;
            data = Material.BLUE_ICE.createBlockData();
        }

        @Override
        public void run() {
            max--;
            projectile.getWorld().spawnParticle(Particle.SNOWBALL, projectile.getLocation(), 2);
            projectile.getWorld().spawnParticle(Particle.BLOCK_CRACK, projectile.getLocation(), 4, 0.0, 0.1, 0.0, data);
            if (max == 0) {
                cancel();
            }
        }
    }

}
