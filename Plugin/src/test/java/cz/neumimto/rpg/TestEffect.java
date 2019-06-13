package cz.neumimto.rpg;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;

@Generate(description = "test", id = "name")
public class TestEffect extends EffectBase<TestModel> {

	public static String name = "Test";
	private IEffectConsumer iEffectConsumer;

	public TestEffect(IEffectConsumer character, long duration, float model) {
		super(name, character);
		setDuration(duration);
	}

	@Override
	public IEffectConsumer getConsumer() {
		return iEffectConsumer;
	}

	@Override
	public <T extends IEffectConsumer> void setConsumer(T consumer) {
		this.iEffectConsumer = consumer;
	}
}
