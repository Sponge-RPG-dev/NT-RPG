package cz.neumimto.effects.positive;


import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.effects.stacking.DoubleEffectStackingStrategy;
import cz.neumimto.rpg.common.scripting.JsBinding;

@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect which redirects incoming % of damage to mana pool")
public class DamageToMana extends EffectBase<Double> {

	public static final String name = "Damage to mana";

	public DamageToMana(IEffectConsumer character, long duration, double percentage) {
		super(name, character);
		setDuration(duration);
		setValue(percentage);
		setStackable(true, DoubleEffectStackingStrategy.INSTANCE);
	}
}
