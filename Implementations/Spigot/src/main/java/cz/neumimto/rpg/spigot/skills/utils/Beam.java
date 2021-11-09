package cz.neumimto.rpg.spigot.skills.utils;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.events.skill.SpigotSkillTargetAttemptEvent;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Beam extends BukkitRunnable {

    private final Set<UUID> skipEntity;
    private final double gravityForce;
    private final ISpigotEntity nCaster;

    private final OnTick onTick;
    private final OnEntityHit onTarget;
    private final OnHitGround onHitGround;

    private final double maxDistance;
    private final MutableBoundingBox box;

    private final LivingEntity caster;
    private final Location loc;
    private final Vector dir;

    private double step;
    private int tick;
    PlayerSkillContext playerSkillContext;
    double beamDistance = 0;

    public Beam(ISpigotEntity caster,
                 double step, double gravityForce, double maxDistance,
                 PlayerSkillContext playerSkillContext,
                 OnTick onTick, OnEntityHit onTarget, OnHitGround onHitGround) {
        this.caster = caster.getEntity();
        this.nCaster = caster;
        this.onTick = onTick;
        this.onTarget = onTarget;
        this.onHitGround = onHitGround;
        this.skipEntity = new HashSet<>();
        this.step = step;
        this.maxDistance = maxDistance;
        this.gravityForce = gravityForce;
        this.playerSkillContext = playerSkillContext;
        this.loc = this.caster.getEyeLocation();
        this.dir = loc.getDirection().normalize().multiply(step);
        this.box = new MutableBoundingBox(loc, step /2);
    }

    public void start(long period) {
        runTaskTimer(SpigotRpgPlugin.getInstance(), 0, period);

       // new BukkitRunnable() {
       //     double t = 0;
       //     @Override
       //     public void run() {
       //         double radius = Math.sin(t);
       //         for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 8) {
       //             double x = Math.sin(angle) * radius;
       //             double z = Math.cos(angle) * radius;
       //             Vector v = new Vector(x, 0, z);
       //             v.rotateAroundX(caster.getLocation().getPitch() + 90F);
       //             v.rotateAroundY(-caster.getLocation().getYaw());
       //             ParticleEffect.VILLAGER_HAPPY.display(0F, 0F, 0F, 0.004F, 1, loc.clone().add(v), 257D);
       //         }
       //         t += Math.PI / 8;
       //         if (t > Math.PI * 2)
       //             t = 0;
       //         loc.add(dir);
       //     }
       // }.runTaskTimer(SpigotRpgPlugin.getInstance(), 0,1);
    }

    @Override
    public void run() {

        MutableBoundingBox box = new MutableBoundingBox(loc, step /2);

        if (beamDistance < maxDistance) {

            beamDistance += step;
            loc.add(dir);

            if (gravityForce != 0) {
                dir.add(new Vector(0, -gravityForce,0));
                loc.setDirection(dir);
            }

            if (!loc.getBlock().isPassable()) {
                if (onHitGround != null) {
                    onHitGround.process(tick, beamDistance,nCaster, playerSkillContext, loc);
                }
                super.cancel();
                return;
            }

            box.moveAt(loc);

            if (onTick != null) {
                onTick.process(tick, nCaster, beamDistance, playerSkillContext, loc, box, dir);
            }

            for (LivingEntity e : loc.getWorld().getLivingEntities()) {
                if (e == caster || e.isDead() || skipEntity.contains(e.getUniqueId()) || !box.overlaps(e.getBoundingBox())) {
                    continue;
                }
                skipEntity.add(e.getUniqueId());

                ISpigotEntity target = (ISpigotEntity) Rpg.get().getEntityService().get(e);
                SpigotSkillTargetAttemptEvent skillTargetAttemptEvent = new SpigotSkillTargetAttemptEvent();
                skillTargetAttemptEvent.setTarget(target);
                skillTargetAttemptEvent.setCaster(nCaster);

                if (Rpg.get().postEvent(skillTargetAttemptEvent)) {
                    continue;
                }

                target = (ISpigotEntity) skillTargetAttemptEvent.getTarget();

                if (onTarget != null) {
                    BeamActionResult process = onTarget.process(target, tick, beamDistance, nCaster, playerSkillContext, loc);
                    if (process == BeamActionResult.STOP) {
                        super.cancel();
                        return;
                    }
                }
            }
            tick++;
        } else {
            super.cancel();
        }
    }

    public enum BeamActionResult {
        CONTINUE, STOP, HIT_GROUND,
    }

    public interface OnEntityHit {

        @ScriptMeta.ScriptTarget
        BeamActionResult process(@ScriptMeta.NamedParam("target") ISpigotEntity target,
                                     @ScriptMeta.NamedParam("tick") int tick,
                                     @ScriptMeta.NamedParam("distance") double distance,
                                     @ScriptMeta.NamedParam("caster") ISpigotEntity caster,
                                     @ScriptMeta.NamedParam("context") PlayerSkillContext context,
                                     @ScriptMeta.NamedParam("location") Location location);
    }

    public interface OnTick {

        @ScriptMeta.ScriptTarget
        void process(@ScriptMeta.NamedParam("tick") int tick,
                     @ScriptMeta.NamedParam("caster") ISpigotEntity caster,
                     @ScriptMeta.NamedParam("distance") double distance,
                     @ScriptMeta.NamedParam("context") PlayerSkillContext context,
                     @ScriptMeta.NamedParam("location") Location location, MutableBoundingBox box, Vector dir);
    }

    public interface OnHitGround {

        @ScriptMeta.ScriptTarget
        void process(@ScriptMeta.NamedParam("tick") int tick,
                        @ScriptMeta.NamedParam("distance") double distance,
                         @ScriptMeta.NamedParam("caster") ISpigotEntity caster,
                         @ScriptMeta.NamedParam("context") PlayerSkillContext context,
                         @ScriptMeta.NamedParam("location") Location location);
    }

}