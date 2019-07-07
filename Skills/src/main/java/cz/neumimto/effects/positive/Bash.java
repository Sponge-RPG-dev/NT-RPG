package cz.neumimto.effects.positive;

import cz.neumimto.model.BashModel;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect, which gives the target % chance to apply stun while attacking to entity")
public class Bash extends EffectBase<BashModel> {

    public static final String name = "Bash";

    public Bash(IEffectConsumer consumer, long duration, BashModel value) {
        super(name, consumer);
        setValue(value);
        setDuration(duration);
        setStackable(true, null);
    }


    @Override
    public IEffectContainer constructEffectContainer() {
        return new EffectContainer.UnstackableSingleInstance(this);
    }
}
