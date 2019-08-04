package cz.neumimto.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;

import java.util.Collections;
import java.util.Set;

/**
 * Created by NeumimTo on 6.2.2016.
 */
@JsBinding(JsBinding.Type.CLASS)
public class SoulBindEffect extends EffectBase implements IEffectContainer {

    private final IEffectConsumer target;

    public SoulBindEffect(IEffectConsumer caster, IEffectConsumer target) {
        super("souldbind", caster);
        this.target = target;
    }

    public IEffectConsumer getTarget() {
        return target;
    }


    @Override
    public Set<SoulBindEffect> getEffects() {
        return Collections.singleton(this);
    }

    @Override
    public Object getStackedValue() {
        return null;
    }

    @Override
    public void setStackedValue(Object o) {

    }

    @Override
    public SoulBindEffect constructEffectContainer() {
        return this;
    }

}
