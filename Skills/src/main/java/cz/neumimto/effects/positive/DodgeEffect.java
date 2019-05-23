package cz.neumimto.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.api.effects.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by ja on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "% chance to dodge incoming physical damage")
public class DodgeEffect extends EffectBase<Float> {

	public static final String name = "Dodge";

	public DodgeEffect(IEffectConsumer character, long duration, float chance) {
		super(name, character);
		setValue(chance);
		setStackable(true, FloatEffectStackingStrategy.INSTANCE);
		setDuration(duration);
	}
}
