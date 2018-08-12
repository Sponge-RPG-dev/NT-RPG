package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.scripting.JsBinding;

@JsBinding(JsBinding.Type.CLASS)
@ClassGenerator.Generate(id = "name", description = "Heals the target after each kill")
public class LifeAfterKillEffect extends EffectBase<Float> {
	public static final String name = "Life after each kill";

	public LifeAfterKillEffect(IEffectConsumer character, long duration, float healedAmount) {
		super(name, character);
		setDuration(duration);
		setValue(healedAmount);
		setStackable(true, new FloatEffectStackingStrategy());
	}
}
