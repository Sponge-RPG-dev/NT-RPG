package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.packetwrapper.PacketHandler;
import cz.neumimto.rpg.spigot.skills.utils.AbstractPacket;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
@ResourceLoader.Skill("ntrpg:glacialcascade")
public class GlacialCascade extends TargetedEntitySkill {

    @Inject
    private SpigotDamageService spigotDamageService;

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.DAMAGE, "level * 2 + 5");
        settings.addExpression(SkillNodes.DISTANCE, "15");
        addSkillType(SkillType.DAMAGE_CHECK_TARGET);
        addSkillType(SkillType.ELEMENTAL);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info) {
        double doubleNodeValue = info.getDoubleNodeValue(SkillNodes.DAMAGE);
        int maxDistance = info.getIntNodeValue(SkillNodes.DISTANCE);
        Effect effect = new Effect(source, (LivingEntity) target.getEntity(), doubleNodeValue, maxDistance);
        effectService.addEffect(effect, this);
        return SkillResult.OK;
    }

    class Effect extends EffectBase {

        public static final String name = "GlacialCascade";
        private final ISpigotEntity consumer;
        private final LivingEntity target;

        private Location location;
        private Location initLoc;
        private double damage;
        private double maxDistance;
        private final List<Integer> spawnedEntities = new ArrayList<>();
        private List<AbstractPacket> packets = new ArrayList<>();
        private final Set<Player> viewers = new HashSet<>();

        public Effect(ISpigotEntity consumer, LivingEntity target, double damage, int maxDistance) {
            super(name, consumer);
            this.consumer = consumer;
            setPeriod(5);
            setDuration(20000);
            this.target = target;
            initLoc = consumer.getEntity().getLocation();
            this.maxDistance = maxDistance * maxDistance;
            this.damage = damage;
        }


        @Override
        public void onTick(IEffect self) {
            if (target.isDead()) {
                setDuration(0);
                return;
            }
            Location tLoc = target.getLocation();
            this.location.add(vec(this.location, tLoc));

            location = getBlockBelowLoc(location.clone().add(0, 2, 0), 4);
            if (location == null) {
                setDuration(0);
                return;
            }
            location.add(0,0.2,0);

            spawnEntity(location);


            if (location.distanceSquared(tLoc) <= 4) {
                spigotDamageService.damage(consumer.getEntity(),
                        target,
                        EntityDamageEvent.DamageCause.CONTACT,
                        damage,
                        false);
                setDuration(0);
            }
            if (initLoc.distanceSquared(location) > maxDistance) {
                setDuration(0);
            }
        }

        public Location getBlockBelowLoc(Location loc, int it) {
            it -=1;
            if (it == 0) {
                return null;
            }
            Location locBelow = loc.subtract(0, 1, 0);
            if(locBelow.getBlock().isPassable()) {
                locBelow = getBlockBelowLoc(locBelow, it);
            }

            return locBelow;
        }

        public Vector vec(Location a, Location b) {
            Vector vector = b.toVector().subtract(a.toVector());
            return vector.normalize().multiply(1.1);
        }

        private void spawnEntity(Location location) {

            int i = PacketHandler.randomGroundIcicle(location, packets);
            spawnedEntities.add(i);

            for (Player viewer : viewers) {
                for (AbstractPacket packet : packets) {
                    packet.sendPacket(viewer);
                }
            }

            location.getWorld().playSound(location, Sound.BLOCK_GLASS_BREAK, 0.75f,0.5f);

            location.getWorld().spawnParticle(Particle.BLOCK_CRACK, location, 10, 0.0, 0.1, 0.0, Material.ICE.createBlockData());
            location.getWorld().spawnParticle(Particle.SNOWBALL, location, 8);


            packets = new ArrayList<>();
        }

        @Override
        public void onApply(IEffect self) {
            LivingEntity entity = consumer.getEntity();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getWorld() == entity.getWorld()
                        && entity.getLocation().distanceSquared(onlinePlayer.getLocation()) <= 500) {
                    viewers.add(onlinePlayer);
                }
            }
            Location eyelocation = entity.getLocation();
            Vector vec = entity.getLocation().getDirection();
            location = eyelocation.add(vec);
        }

        @Override
        public void onRemove(IEffect self) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    SpigotRpgPlugin.getInstance(),
                    () -> PacketHandler.destroy(spawnedEntities, viewers),
                    40);
        }
    }
}
