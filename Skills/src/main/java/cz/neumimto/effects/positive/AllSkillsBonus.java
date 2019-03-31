package cz.neumimto.effects.positive;

import cz.neumimto.rpg.effects.*;
import cz.neumimto.rpg.effects.common.stacking.IntegerEffectStackingStrategy;
import cz.neumimto.rpg.properties.DefaultProperties;
import cz.neumimto.rpg.scripting.JsBinding;

@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect which gives +x bonus points to all skill")
public class AllSkillsBonus extends EffectBase<Integer> {

	public static final String name = "All skill";

	public AllSkillsBonus(IEffectConsumer character, long duration, int value) {
		super(name, character);
		setDuration(duration);
		setStackable(true, new IntegerEffectStackingStrategy());
		setValue(value);
	}

	@Override
	public void onApply(IEffect self) {
		getConsumer().setProperty(DefaultProperties.all_skills_bonus, getConsumer().getProperty(DefaultProperties.all_skills_bonus) + getValue());
	}

	@Override
	public void onRemove(IEffect self) {
		getConsumer().setProperty(DefaultProperties.all_skills_bonus, getConsumer().getProperty(DefaultProperties.all_skills_bonus) - getValue());
	}

}
