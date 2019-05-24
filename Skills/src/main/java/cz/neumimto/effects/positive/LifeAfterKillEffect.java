package cz.neumimto.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.api.effects.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.common.scripting.JsBinding;

@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Heals the target after each kill")
public class LifeAfterKillEffect extends EffectBase<Float> {

	public static final String name = "Life after each kill";

	public LifeAfterKillEffect(IEffectConsumer character, long duration, float healedAmount) {
		super(name, character);
		setDuration(duration);
		setValue(healedAmount);
		setStackable(true, FloatEffectStackingStrategy.INSTANCE);
	}
}
