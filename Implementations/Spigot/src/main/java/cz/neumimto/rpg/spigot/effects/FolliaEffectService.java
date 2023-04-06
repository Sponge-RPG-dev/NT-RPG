package cz.neumimto.rpg.spigot.effects;

import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FolliaEffectService extends EffectService {

    private ScheduledTask asyncTask;

    public FolliaEffectService() {
        pendingAdditions = ConcurrentHashMap.newKeySet();
        pendingRemovals = ConcurrentHashMap.newKeySet();
        effectSet = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void startEffectScheduler() {
        asyncTask = Bukkit.getServer().getAsyncScheduler()
                .runAtFixedRate(SpigotRpgPlugin.getInstance(), scheduledTask -> {schedule();}, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void runEffect(IEffect effect) {
        pendingAdditions.add(effect);
        LivingEntity entity = (LivingEntity) effect.getConsumer().getEntity();
        entity.getScheduler().execute(asyncTask.getOwningPlugin(), () -> effect.onApply(effect), null, 0L);
    }

    public void schedule() {
        for (IEffect pendingRemoval : pendingRemovals) {

            removeEffectContainer(pendingRemoval.getEffectContainer(), pendingRemoval, pendingRemoval.getConsumer());

            effectSet.remove(pendingRemoval);
        }

        pendingRemovals.clear();
        long l = System.currentTimeMillis();
        for (IEffect e : effectSet) {
            if (!mayTick(e)) {
                pendingRemovals.add(e);
                continue;
            }
            if ((e.getPeriod() > 0 && !e.isTickingDisabled()) && e.getPeriod() + e.getLastTickTime() <= l) {
                tickEffect(e, l);
            }

            if (e.getDuration() == unlimited_duration) {
                continue;
            }

            if (e.getExpireTime() <= l) {
                removeEffect(e, e.getConsumer());
            }
        }

        effectSet.addAll(pendingAdditions);
        pendingAdditions.clear();
    }

    @Override
    public void removeEffect(IEffect effect, IEffectConsumer consumer) {
        LivingEntity le = (LivingEntity) effect.getConsumer().getEntity();
        le.getScheduler().run(asyncTask.getOwningPlugin(), scheduledTask -> {
            super.removeEffect(effect, consumer);
        }, null);
    }

    public void tickEffect(IEffect effect, long time) {
        LivingEntity le = (LivingEntity) effect.getConsumer().getEntity();
        le.getScheduler().run(asyncTask.getOwningPlugin(), scheduledTask -> {
            super.tickEffect(effect, time);
        }, null);
    }


    protected void removeEffectContainer(IEffectContainer container, IEffect effect, IEffectConsumer consumer) {
        LivingEntity le = (LivingEntity) consumer.getEntity();
        le.getScheduler().run(asyncTask.getOwningPlugin(), scheduledTask -> {
            super.removeEffectContainer(container, effect, consumer);
        }, null);
    }

    @Override
    public void stopEffectScheduler() {
        asyncTask.cancel();
    }
}
