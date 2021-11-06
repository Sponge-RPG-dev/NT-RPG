package cz.neumimto.rpg.spigot.skills.utils;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.events.skill.SkillTargetAttemptEvent;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
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
    private final IEntity nCaster;

    private final OnTick onTick;
    private final OnEntityHit onTarget;
    private final OnHitGround onHitGround;

    private final double maxDistance;

    private LivingEntity caster;
    private Location startLoc;

    private Location currentLoc;

    private double step;
    private int tick;
    PlayerSkillContext playerSkillContext;
    double beamDistance = 0;

    public Beam(IEntity caster,
                 double step, double gravityForce, double maxDistance,
                 PlayerSkillContext playerSkillContext,
                 OnTick onTick, OnEntityHit onTarget, OnHitGround onHitGround) {
        this.caster = (LivingEntity) caster.getEntity();
        this.nCaster = caster;
        this.startLoc = this.caster.getEyeLocation().clone();
        this.currentLoc = startLoc.clone();
        this.onTick = onTick;
        this.onTarget = onTarget;
        this.onHitGround = onHitGround;
        this.skipEntity = new HashSet<>();
        this.step = step;
        this.maxDistance = maxDistance;
        this.gravityForce = gravityForce;
        this.playerSkillContext = playerSkillContext;
    }

    public void start(long period) {
        runTaskTimer(SpigotRpgPlugin.getInstance(), 0, period);
    }

    @Override
    public void run() {
        Vector dir = startLoc.getDirection().multiply(step);

        MutableBoundingBox box = new MutableBoundingBox(currentLoc, step /2);



        if (beamDistance < maxDistance) {

            beamDistance += step;
            currentLoc.add(dir);

            if (gravityForce != 0) {
                dir.add(new Vector(0, -gravityForce,0));
                currentLoc.setDirection(dir);
            }

            if (!currentLoc.getBlock().isPassable()) {
                if (onHitGround != null) {
                    onHitGround.process(tick, beamDistance,nCaster, playerSkillContext, currentLoc);
                }
                super.cancel();
                return;
            }

            if (onTick != null) {
                onTick.process(tick, nCaster, beamDistance, playerSkillContext, currentLoc);
            }

            box.moveAt(currentLoc);

            for (LivingEntity e : startLoc.getWorld().getLivingEntities()) {
                if (e == caster || e.isDead() || skipEntity.contains(e.getUniqueId()) || !box.overlaps(e.getLocation())) {
                    continue;
                }
                skipEntity.add(e.getUniqueId());

                IEntity target = Rpg.get().getEntityService().get(e);
                SkillTargetAttemptEvent skillTargetAttemptEvent = new SpigotSkillTargetAttemptEvent();
                skillTargetAttemptEvent.setTarget(target);
                skillTargetAttemptEvent.setCaster(nCaster);

                if (Rpg.get().postEvent(skillTargetAttemptEvent)) {
                    continue;
                }

                target = skillTargetAttemptEvent.getTarget();

                if (onTarget != null) {
                    BeamActionResult process = onTarget.process(target, tick, beamDistance, nCaster, playerSkillContext, currentLoc);
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
        BeamActionResult process(@ScriptMeta.NamedParam("target") IEntity target,
                                     @ScriptMeta.NamedParam("tick") int tick,
                                     @ScriptMeta.NamedParam("distance") double distance,
                                     @ScriptMeta.NamedParam("caster") IEntity caster,
                                     @ScriptMeta.NamedParam("context") PlayerSkillContext context,
                                     @ScriptMeta.NamedParam("location") Location location);
    }

    public interface OnTick {

        @ScriptMeta.ScriptTarget
        void process(@ScriptMeta.NamedParam("tick") int tick,
                         @ScriptMeta.NamedParam("caster") IEntity caster,
                         @ScriptMeta.NamedParam("distance") double distance,
                         @ScriptMeta.NamedParam("context") PlayerSkillContext context,
                         @ScriptMeta.NamedParam("location") Location location);
    }

    public interface OnHitGround {

        @ScriptMeta.ScriptTarget
        void process(@ScriptMeta.NamedParam("tick") int tick,
                        @ScriptMeta.NamedParam("distance") double distance,
                         @ScriptMeta.NamedParam("caster") IEntity caster,
                         @ScriptMeta.NamedParam("context") PlayerSkillContext context,
                         @ScriptMeta.NamedParam("location") Location location);
    }

}