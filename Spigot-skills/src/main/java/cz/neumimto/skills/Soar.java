package cz.neumimto.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.CommonEffectTypes;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.UnstackableEffectBase;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.skills.effects.negative.StunEffect;
import cz.neumimto.skills.effects.positive.FeatherFall;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

@Singleton
@ResourceLoader.Skill("ntrpg:soar")
public class Soar extends ActiveSkill<ISpigotCharacter> {
    static private int tickDelay = 15;

    @Inject
    private SpigotEntityService spigotEntityService;

    @Inject
    private EffectService effectService;

    @Inject
    private SpigotEntityService entityService;

    @Inject
    private SpigotDamageService damageService;

    @Override
    public void init() {
        super.init();

        setDamageType(MAGIC.name());
        settings.addNode(SkillNodes.DAMAGE, 10, 10);
        settings.addNode("drop-damage", 10f, .5f);
        settings.addNode("stun-duration", 10000, 1000);
        settings.addNode(SkillNodes.RADIUS, 5, 0.5f);

        addSkillType(SkillType.MOVEMENT);
        addSkillType(SkillType.AOE);
        addSkillType(SkillType.ESCAPE);
        addSkillType(SkillType.TELEPORT);


    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext info, SkillContext skillContext) {

        final Player player = character.getPlayer();

        double radius = skillContext.getDoubleNodeValue(SkillNodes.RADIUS);
        double dashDmg = skillContext.getDoubleNodeValue(SkillNodes.DAMAGE);
        double dropDmg = skillContext.getDoubleNodeValue("drop-damage");
        long stunTime = skillContext.getLongNodeValue("stun-duration");

        Set<LivingEntity> targets = new HashSet<>();
        for (Entity nearbyEntity : player.getNearbyEntities(radius, 3, radius)) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                if (damageService.canDamage(character, livingEntity)) {
                    livingEntity.setVelocity(new Vector(0, 1, 0));
                    IEntity iEntity = spigotEntityService.get(livingEntity);
                    effectService.addEffect(new SoaringLevitationEffect(iEntity, livingEntity, stunTime, dropDmg, player), this);
                    targets.add(livingEntity);
                }
            }
        }

        if (!targets.isEmpty()) {
            player.setVelocity(new Vector(0, 1, 0));
            Bukkit.getScheduler().scheduleSyncDelayedTask(SpigotRpgPlugin.getInstance(), () -> {
                effectService.addEffect(new SoaringStrikeEffect(character, targets, dashDmg), this);
            }, 15);
        }
        skillContext.next(character, info, skillContext.result(SkillResult.OK));
    }

    public static String name = "SoarStrikeEffect";

    private static int MAX_NO_MOVING_TICKS = 2;

    public class SoaringStrikeEffect extends UnstackableEffectBase {

        private final Iterator<LivingEntity> affected;
        private Location current;
        private LivingEntity entityCaster;
        private LivingEntity currentTarget;
        private double dashDamage;
        private Location last;
        private int ticks;
        private BukkitRunnable runnable;

        public SoaringStrikeEffect(ISpigotEntity character, Set<LivingEntity> affected, double dashDamage) {
            super(name, character);
            this.affected = affected.iterator();
            this.entityCaster = character.getEntity();
            this.current = entityCaster.getLocation().clone();
            this.last = current.clone();
            this.dashDamage = dashDamage;
            this.setPeriod(0);
            this.setDuration(10000);
            currentTarget = this.affected.next();
        }

        @Override
        public void onApply(IEffect self) {
            entityCaster.getWorld().playSound(current, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0F, 1.0F);
            runnable = new BukkitRunnable() {

                @Override
                public void run() {
                    onTick();
                }
            };
            runnable.runTaskTimer(SpigotRpgPlugin.getInstance(), 0L, 2L);
        }

        @Override
        public void onRemove(IEffect self) {
            runnable.cancel();
        }

        public void onTick() {
            ticks++;
            Location casterLoc = entityCaster.getLocation();
            Location targetLocation = currentTarget.getLocation();
            double distSquared = casterLoc.distanceSquared(targetLocation);
            if (currentTarget.isDead() || distSquared > 400) {
                if (affected.hasNext()) {
                    currentTarget = affected.next();
                } else {
                    setDuration(0);
                    return;
                }
            }


            Vector pos = targetLocation.toVector();
            Vector target = casterLoc.toVector();
            Vector velocity = target.subtract(pos);
            entityCaster.setVelocity(velocity.normalize().multiply(-1.25));

            boolean hasHit = false;

            double movementLastTick = current.distanceSquared(last);
            if (movementLastTick <= 4D) {
                ticks++;
                if (ticks > 2) {
                    updateTargetOrStop();
                    return;
                }
            }

            if (distSquared < 2D) {

                damageService.damage(entityCaster, currentTarget, ENTITY_ATTACK, dashDamage, false);
                currentTarget.getWorld().playSound(targetLocation, Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                currentTarget.getWorld().spawnParticle(Particle.BLOCK_CRACK, targetLocation, 30, 2, 2, 2, Material.REDSTONE_BLOCK.getData());
                IEntity targAle = entityService.get(currentTarget);
                SoaringLevitationEffect effect = (SoaringLevitationEffect) targAle.getEffect(SoarStrikeAffected_NAME);
                if (effect != null) {
                    effect.setDuration(0);
                }

                hasHit = true;
            }
            if (hasHit || currentTarget.isDead()) {
                updateTargetOrStop();
            }
            last = current;
            current = casterLoc;
        }

        protected boolean updateTargetOrStop() {
            if (!affected.hasNext()) {
                setDuration(0);
                return true;
            }
            currentTarget = affected.next();
            return false;
        }

    }

    private String SoarStrikeAffected_NAME = "SoarStrikeEffect_NEG";

    private class SoaringLevitationEffect extends UnstackableEffectBase {

        private final LivingEntity l;
        long stunDuration;
        double damage;
        private final LivingEntity entityCaster;
        private Location loc;

        public SoaringLevitationEffect(IEntity entity, LivingEntity l,
                                       long stunDuration, double damage, LivingEntity entityCaster) {
            super(SoarStrikeAffected_NAME, entity);
            this.l = l;
            this.stunDuration = stunDuration;
            this.damage = damage;
            this.entityCaster = entityCaster;
            addEffectType(CommonEffectTypes.SILENCE);
            setDuration(10000);
            setPeriod(20);
        }

        @Override
        public void onApply(IEffect self) {

        }

        @Override
        public void onTick(IEffect self) {
            if (getLastTickTime() + 500L > System.currentTimeMillis() && l.hasGravity()) {
                l.setVelocity(new Vector(0, 0, 0));
                l.setGravity(false);
                loc = l.getLocation();
            } else if (loc != null && l.getLocation().distanceSquared(loc) <= 2) {
                setDuration(0);

            }

            if (l.isDead()) {
                setDuration(0);
                return;
            }

            if (l.isSwimming()) {
                setDuration(0);
                return;
            }
            if (l.isOnGround()) {
                ISpigotEntity en = (ISpigotEntity) getConsumer();
                if (!en.hasEffect(FeatherFall.name)) {
                    if (damage > 0) {
                        damageService.damage(entityCaster, l, FALL, damage, false);
                    }
                    if (stunDuration > 0) {
                        effectService.addEffect(new StunEffect(en, stunDuration), Soar.this);
                    }
                }
                setDuration(0);
            }
        }

        @Override
        public void onRemove(IEffect self) {
            l.setGravity(true);
        }
    }
}