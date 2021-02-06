package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.FireworkHandler;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Singleton
@ResourceLoader.Skill("ntrpg:fireblast")
public class FireBlast extends TargetedBlockSkill {

    public static Set<Material> HOT_BLOCKS;

    static {
        HOT_BLOCKS = new HashSet<>();
        HOT_BLOCKS.add(Material.MAGMA_BLOCK);
        HOT_BLOCKS.add(Material.LAVA);
        HOT_BLOCKS.add(Material.CAMPFIRE);
        HOT_BLOCKS.add(Material.SOUL_CAMPFIRE);
        HOT_BLOCKS.add(Material.SOUL_SAND);
        HOT_BLOCKS.add(Material.SOUL_TORCH);
        HOT_BLOCKS.add(Material.TORCH);
    }

    @Inject
    private SpigotDamageService spigotDamageService;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.RADIUS, "5");
        settings.addExpression(SkillNodes.DAMAGE, "level * 3 + 5");
        settings.addExpression("hot-blocks-multipler", "1 + 0.3 * level");
        addSkillType(SkillType.FIRE);
    }

    @Override
    protected SkillResult castOn(Block block, ISpigotCharacter character, PlayerSkillContext skillContext) {
        int blastradius = skillContext.getIntNodeValue("blast-radius");

        Location location = block.getLocation();
        double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        if (HOT_BLOCKS.contains(block.getType())) {
            damage *= skillContext.getDoubleNodeValue("hot-blocks-multipler");
            new Particles(location).runTaskTimer(SpigotRpgPlugin.getInstance(), 0, 1);
        }

        Player player = character.getPlayer();
        World world = block.getWorld();
        Location loc = block.getLocation().add(0,1,0);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.5f);

        Collection<Entity> nearbyEntities = world.getNearbyEntities(loc, blastradius, blastradius, blastradius,
                entity -> !entity.isDead() && entity instanceof LivingEntity && entity != player);

        spawnFireworks(loc);

        for (Entity nearbyEntity : nearbyEntities) {
            LivingEntity l = (LivingEntity) nearbyEntity;
            if (spigotDamageService.canDamage(character, l)) {
                spigotDamageService.damage(player, l, EntityDamageEvent.DamageCause.FIRE, damage, false);
                push(loc, l);
            }
        }

        return SkillResult.OK;
    }

    public void push(Location loc, Entity entity) {
        Vector vector = entity.getLocation().toVector().subtract(loc.toVector());
        double force = Math.abs((30 - loc.distance(entity.getLocation())) * 0.125);
        vector.normalize();
        vector.multiply(force);
        entity.setVelocity(vector);
    }

    private void spawnFireworks(Location location){
        FireworkHandler.spawn(location, FireworkEffect.builder()
                .withColor(Color.fromRGB( 229, 55, 28 ))
                .withFade(Color.fromRGB( 229, 125, 28 ))
                .with(FireworkEffect.Type.BALL)
                .build(), 40);
    }

    private class Particles extends BukkitRunnable {

        private Location location;
        private int c = 200;
        public Particles(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            c--;
            location.getWorld().spawnParticle(Particle.FLAME, location, 2,0,0.1,0);
            if (c == 0) {
                cancel();
            }
        }
    }
}
