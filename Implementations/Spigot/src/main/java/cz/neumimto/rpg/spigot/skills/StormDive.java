package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.effects.SpigotEffectService;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import cz.neumimto.rpg.spigot.packetwrapper.PacketHandler;
import cz.neumimto.rpg.spigot.skills.utils.AbstractPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:stormdive")
public class StormDive extends TargetedBlockSkill {

    @Inject
    private SpigotEffectService effectService;

    @Inject
    private SpigotDamageService damageService;

    @Override
    public void init() {
        super.init();
        settings.addExpression(SkillNodes.DAMAGE, "level *2 + 20");
        settings.addExpression("land-damage-range", "5");
        addSkillType(SkillType.MOVEMENT);
        addSkillType(SkillType.AOE);
        addSkillType(SkillType.UTILITY);
    }

    @Override
    protected SkillResult castOn(Block block, BlockFace blockFace, SpigotCharacter character, PlayerSkillContext skillContext) {
        if (block.getLocation().getY() + 2 < character.getPlayer().getEyeLocation().getY()) {
            double damage = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
            double range = skillContext.getDoubleNodeValue("land-damage-range");
            Effect effect = new Effect(character, damage, range, block.getLocation());
            effectService.addEffect(effect, this);
            return SkillResult.OK;
        } else {
            return SkillResult.CANCELLED;
        }
    }

    public class Effect extends EffectBase {

        private final float pitch;
        private final float yaw;
        private LivingEntity entity;
        private SpigotCharacter character;
        private double damage;
        private double range;
        private final Vector velocity;
        private final Location targetLoc;

        public Effect(SpigotCharacter consumer, double damage, double range, Location targetLoc) {
            super("LightningWarpEffect", consumer);
            this.entity = consumer.getEntity();
            this.character = consumer;
            this.damage = damage;
            this.range = range;

            Location eyeLocation = entity.getEyeLocation();
            yaw = eyeLocation.getYaw();
            pitch = eyeLocation.getPitch();
            velocity = targetLoc.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(2);
            this.targetLoc = targetLoc;
            setDuration(15000);
            setPeriod(20);
        }

        @Override
        public void onApply(IEffect self) {
            AbstractPacket riptide = PacketHandler.riptide(entity);
            entity.setGravity(false);
            Bukkit.getServer().getOnlinePlayers().forEach(riptide::sendPacket);
            entity.setVelocity(velocity);
        }

        @Override
        public void onTick(IEffect self) {
            Player caster = character.getPlayer();

            Vector c = caster.getLocation().toVector().subtract(targetLoc.toVector());
            Vector d = caster.getEyeLocation().getDirection();
            double delta = c.dot(d);

            Location add = caster.getLocation().clone().add(d.normalize());


            if (caster.isSneaking()
                    || caster.isInWater()
                    || delta > 0
                    || targetLoc.distanceSquared(caster.getLocation()) <= 4
                    || !add.getBlock().isPassable()) {

                List<Entity> nearbyEntities = entity.getNearbyEntities(range, range, range);
                for (Entity nearbyEntity : nearbyEntities) {
                    if (nearbyEntity instanceof LivingEntity) {
                        LivingEntity l = (LivingEntity) nearbyEntity;
                        if (damageService.damage(l, caster, EntityDamageEvent.DamageCause.LIGHTNING, damage, false)) {
                            l.getLocation().getWorld().strikeLightningEffect(l.getLocation());
                        }
                    }
                }
                setDuration(0);
            } else {
                Location eyeLocation = entity.getEyeLocation();
                eyeLocation.setPitch(pitch);
                eyeLocation.setYaw(yaw);
                entity.setVelocity(velocity);
            }
            entity.getWorld().spawnParticle(Particle.CLOUD, entity.getLocation(), 15);
            entity.getWorld().spawnParticle(Particle.WATER_DROP, entity.getLocation(), 5);
        }

        @Override
        public void onRemove(IEffect self) {
            AbstractPacket riptide = PacketHandler.riptideEnd(entity);
            entity.setGravity(true);
            Bukkit.getServer().getOnlinePlayers().forEach(riptide::sendPacket);
        }
    }
}
