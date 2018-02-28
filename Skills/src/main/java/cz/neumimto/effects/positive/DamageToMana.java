package cz.neumimto.effects.positive;


import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.DoubleEffectStackingStrategy;
import cz.neumimto.rpg.utils.Utils;

@ClassGenerator.Generate(id = "name")
public class DamageToMana extends EffectBase<Double> {

	public static final String name = "Damage to mana";

	public DamageToMana(IEffectConsumer character, long duration, double percentage) {
		super(name, character);
		setDuration(duration);
		setValue(percentage);
		setStackable(true, new DoubleEffectStackingStrategy());
	}
}
