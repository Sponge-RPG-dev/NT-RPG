package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;

/**
 * Created by ja on 6.7.2017.
 */
@ClassGenerator.Generate(id = "name", description = "% chance to dodge incoming physical damage")
public class DodgeEffect extends EffectBase<Float> {

	public static final String name = "Dodge";

	public DodgeEffect(IEffectConsumer character, long duration, float chance) {
		super(name, character);
		setValue(chance);
		setStackable(true, new FloatEffectStackingStrategy());
		setDuration(duration);
	}
}
