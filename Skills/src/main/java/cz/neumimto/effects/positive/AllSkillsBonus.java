package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.IntegerEffectStackingStrategy;
import cz.neumimto.rpg.players.properties.DefaultProperties;

@ClassGenerator.Generate(id = "name",description = "An effect which gives +x bonus points to all skills")
public class AllSkillsBonus extends EffectBase<Integer> {

	public static final String name = "All skills";

	public AllSkillsBonus(IEffectConsumer character, long duration, int value) {
		super(name, character);
		setDuration(duration);
		setStackable(true, new IntegerEffectStackingStrategy());
		setValue(value);
	}

	@Override
	public void onApply() {
		getConsumer().setProperty(DefaultProperties.all_skills_bonus, getConsumer().getProperty(DefaultProperties.all_skills_bonus) + getValue());
	}

	@Override
	public void onRemove() {
		getConsumer().setProperty(DefaultProperties.all_skills_bonus, getConsumer().getProperty(DefaultProperties.all_skills_bonus) - getValue());
	}

}
