package cz.neumimto.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.DoubleEffectStackingStrategy;

/**
 * Created by fs on 21.9.15.
 */
@Generate(id = "name", description = "An effect which decreases skill's manacost")
public class ManacostReduction extends EffectBase<Double> {

	public static String name = "Manacost Reduction";

	public ManacostReduction(IEffectConsumer character, long duration, double value) {
		super(name, character);
		setDuration(duration);
		setValue(value);
		setStackable(true, DoubleEffectStackingStrategy.INSTANCE);
	}
}
