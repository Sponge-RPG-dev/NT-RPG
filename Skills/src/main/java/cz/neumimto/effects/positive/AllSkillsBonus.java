package cz.neumimto.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.stacking.IntegerEffectStackingStrategy;
import cz.neumimto.rpg.common.scripting.JsBinding;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;

@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect which gives +x bonus points to all skill")
public class AllSkillsBonus extends EffectBase<Integer> {

	public static final String name = "All skill";

	public AllSkillsBonus(IEffectConsumer character, long duration, int value) {
		super(name, character);
		setDuration(duration);
		setStackable(true, IntegerEffectStackingStrategy.INSTANCE);
		setValue(value);
	}

	@Override
	public void onApply(IEffect self) {
		getConsumer().setProperty(SpongeDefaultProperties.all_skills_bonus, getConsumer().getProperty(SpongeDefaultProperties.all_skills_bonus) + getValue());
	}

	@Override
	public void onRemove(IEffect self) {
		getConsumer().setProperty(SpongeDefaultProperties.all_skills_bonus, getConsumer().getProperty(SpongeDefaultProperties.all_skills_bonus) - getValue());
	}

}
