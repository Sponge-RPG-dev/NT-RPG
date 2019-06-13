package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;

/**
 * Created by NeumimTo on 25.2.2018.
 */
@Generate(id = "name", description = "test")
public class TestEffectFloat extends SpongeEffectBase<Float> {

	public static String name = "Test";

	public TestEffectFloat(IEffectConsumer character, long duration, float testModel) {
		super(name, character);
		setDuration(duration);
		setValue(testModel);
	}

}
