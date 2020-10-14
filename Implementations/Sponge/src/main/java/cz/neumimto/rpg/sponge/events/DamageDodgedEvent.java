package cz.neumimto.rpg.sponge.events;

import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.effects.positive.DodgeEffect;
import org.spongepowered.api.event.Cancellable;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class DamageDodgedEvent extends AbstractNEvent implements Cancellable {

    private final IEntity source;
    private final IEntity target;
    private final IEffectContainer<Float, DodgeEffect> effect;
    private boolean cancelled;

    public DamageDodgedEvent(IEntity source, IEntity target, IEffectContainer<Float, DodgeEffect> effect) {
        this.source = source;
        this.target = target;
        this.effect = effect;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public IEntity getSource() {
        return source;
    }

    public IEntity getTarget() {
        return target;
    }

    public IEffectContainer<Float, DodgeEffect> getEffect() {
        return effect;
    }
}
