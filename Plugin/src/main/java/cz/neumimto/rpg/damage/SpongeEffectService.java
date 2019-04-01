package cz.neumimto.rpg.damage;

import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.effect.EffectApplyEvent;
import cz.neumimto.rpg.events.effect.EffectRemoveEvent;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.scheduler.Task;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
public class SpongeEffectService extends EffectService {

    @Inject
    private CauseStackManager causeStackManager;

    @Inject
    private Game game;

    private Task effectTask;

    public void startEffectScheduler() {
        effectTask = game.getScheduler().createTaskBuilder().name("EffectTask")
                .delay(5L, TimeUnit.MILLISECONDS)
                .interval(TICK_PERIOD, TimeUnit.MILLISECONDS)
                .execute(this::schedule)
                .submit(plugin);
    }


    public void stopEffectScheduler() {
        effectTask.cancel();
    }

    @Override
    protected void removeEffectContainer(IEffectContainer container, IEffect effect, IEffectConsumer consumer) {

        try (CauseStackManager.StackFrame frame = causeStackManager.pushCauseFrame()) {
            EffectRemoveEvent<IEffect> event = new EffectRemoveEvent<>(effect);
            causeStackManager.pushCause(effect);

            event.setCause(causeStackManager.getCurrentCause());
            Sponge.getEventManager().post(event);
        }
        super.removeEffectContainer(container, effect, consumer);
    }

    @Override
    public boolean addEffect(IEffect effect, IEffectSourceProvider effectSourceProvider, IEntity entitySource) {
        effect.setEffectSourceProvider(effectSourceProvider);
        EffectApplyEvent event = new EffectApplyEvent(effect);
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
