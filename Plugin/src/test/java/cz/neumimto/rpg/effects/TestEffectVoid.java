package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;

/**
 * Created by NeumimTo on 25.2.2018.
 */
@Generate(id = "name", description = "test")
public class TestEffectVoid extends EffectBase {

	public static String name = "Test";

	public TestEffectVoid(IEffectConsumer character, long duration) {
		super(name, character);
		setDuration(duration);

	}

}