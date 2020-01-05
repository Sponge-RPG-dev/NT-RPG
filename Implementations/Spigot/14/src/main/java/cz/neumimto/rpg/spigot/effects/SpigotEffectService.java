package cz.neumimto.rpg.spigot.effects;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.common.effects.AbstractEffectService;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Singleton;

@Singleton
public class SpigotEffectService extends AbstractEffectService {


    private BukkitRunnable bukkitRunnable;

    @Override
    protected boolean mayTick(IEffect e) {
        return !e.getConsumer().isDetached();
    }

    @Override
    public void startEffectScheduler() {
        bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                schedule();
            }
        };
        bukkitRunnable.runTaskTimer(SpigotRpgPlugin.getInstance(), 5L, 5L);
    }

    @Override
    public void stopEffectScheduler() {
        bukkitRunnable.cancel();
    }
}
