package cz.neumimto.rpg;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;

@Generate(description = "test", id = "name")
public class TestEffect extends EffectBase<TestModel> {

	public static String name = "Test";

	public TestEffect(IEffectConsumer character, long duration, float model) {
		super(name, character);
		setDuration(duration);
	}

}
