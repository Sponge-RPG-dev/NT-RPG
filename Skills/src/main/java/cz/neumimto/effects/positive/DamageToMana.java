package cz.neumimto.effects.positive;


import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.DoubleEffectStackingStrategy;

@ClassGenerator.Generate(id = "name", description = "An effect which redirects incoming % of damage to mana pool")
public class DamageToMana extends EffectBase<Double> {

	public static final String name = "Damage to mana";

	public DamageToMana(IEffectConsumer character, long duration, double percentage) {
		super(name, character);
		setDuration(duration);
		setValue(percentage);
		setStackable(true, new DoubleEffectStackingStrategy());
	}
}
