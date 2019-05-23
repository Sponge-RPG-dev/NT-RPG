package cz.neumimto.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.api.effects.stacking.ValueProcessor;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect which cancels all incoming damage to the target, if attacking player has manapool "
		+ "below specific % value")
public class DampenEffect extends EffectBase<Double> {

	public static final String name = "Dampen";

	public DampenEffect(IEffectConsumer consumer, long duration, double value) {
		super(name, consumer);
		setDuration(duration);
		setValue(value);
		setStackable(true, null);
	}


	@Override
	@SuppressWarnings("unchecked")
	public IEffectContainer constructEffectContainer() {
		return new ValueProcessor.D_MIN(this);
	}
}
