package cz.neumimto.rpg.spigot.effects;

import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.effects.IEffectSourceProvider;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.events.character.SpigotEffectApplyEvent;
import cz.neumimto.rpg.spigot.events.character.SpigotEffectRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Singleton;

@Singleton
public class SpigotEffectService extends EffectService {


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

    @Override
    protected void removeEffectContainer(IEffectContainer container, IEffect effect, IEffectConsumer consumer) {
        SpigotEffectRemoveEvent event = new SpigotEffectRemoveEvent(effect);
        Bukkit.getPluginManager().callEvent(event);
        super.removeEffectContainer(container, effect, consumer);
    }

    @Override
    public boolean addEffect(IEffect effect, IEffectSourceProvider effectSourceProvider, IEntity entitySource) {
        effect.setEffectSourceProvider(effectSourceProvider);

        SpigotEffectApplyEvent event = new SpigotEffectApplyEvent(effect, effectSourceProvider, entitySource);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        return super.addEffect(effect, effectSourceProvider, entitySource);
    }
}
