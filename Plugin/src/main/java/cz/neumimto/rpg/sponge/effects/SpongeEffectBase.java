package cz.neumimto.rpg.sponge.effects;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;

@JsBinding(JsBinding.Type.CLASS)
public class SpongeEffectBase<VALUE> extends EffectBase<VALUE> {

    private IEffectConsumer entity;

    public SpongeEffectBase(String name, IEffectConsumer consumer) {
        super(name, consumer);
    }

    @Override
    public IEffectConsumer getConsumer() {
        return entity;
    }

    @Override
    public void setConsumer(IEffectConsumer consumer) {
        this.entity = consumer;
    }
}
