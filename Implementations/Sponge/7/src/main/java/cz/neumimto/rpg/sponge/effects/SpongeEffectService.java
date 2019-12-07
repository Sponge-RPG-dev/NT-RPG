package cz.neumimto.rpg.sponge.effects;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.common.effects.AbstractEffectService;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.events.effects.SpongeEffectApplyEvent;
import cz.neumimto.rpg.sponge.events.effects.SpongeEffectRemoveEvent;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.scheduler.Task;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class SpongeEffectService extends AbstractEffectService {

    @Inject
    private CauseStackManager causeStackManager;

    @Inject
    private Game game;

    @Inject
    private SpongeRpgPlugin plugin;

    private Task effectTask;

    @Override
    public void startEffectScheduler() {
        effectTask = game.getScheduler().createTaskBuilder().name("EffectTask")
                .delay(5L, TimeUnit.MILLISECONDS)
                .interval(TICK_PERIOD, TimeUnit.MILLISECONDS)
                .execute(this::schedule)
                .submit(plugin);
    }


    @Override
    public void stopEffectScheduler() {
        effectTask.cancel();
    }

    @Override
    protected void removeEffectContainer(IEffectContainer container, IEffect effect, IEffectConsumer consumer) {
        try (CauseStackManager.StackFrame frame = causeStackManager.pushCauseFrame()) {
            SpongeEffectRemoveEvent event = new SpongeEffectRemoveEvent();
            event.setEffect(effect);
            causeStackManager.pushCause(effect);

            event.setCause(causeStackManager.getCurrentCause());
            Sponge.getEventManager().post(event);
        }
        super.removeEffectContainer(container, effect, consumer);
    }

    @Override
    public boolean addEffect(IEffect effect, IEffectSourceProvider effectSourceProvider, IEntity entitySource) {
        effect.setEffectSourceProvider(effectSourceProvider);
        SpongeEffectApplyEvent event = new SpongeEffectApplyEvent();
        event.setEffect(effect);
        try (CauseStackManager.StackFrame frame = causeStackManager.pushCauseFrame()) {
            causeStackManager.pushCause(effect);
            causeStackManager.pushCause(effectSourceProvider);
            if (entitySource != null) {
                causeStackManager.pushCause(entitySource);
            }

            event.setCause(causeStackManager.getCurrentCause());
            if (Sponge.getEventManager().post(event)) {
                return false;
            }
        }
        return super.addEffect(effect, effectSourceProvider, entitySource);
    }
}
