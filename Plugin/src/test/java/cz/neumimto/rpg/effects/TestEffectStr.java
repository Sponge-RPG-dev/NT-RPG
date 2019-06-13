package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;

/**
 * Created by NeumimTo on 25.2.2018.
 */
@Generate(id = "name", description = "test")
public class TestEffectStr extends SpongeEffectBase<String> {

	public static String name = "Test";

	public TestEffectStr(IEffectConsumer character, long duration, String testModel) {
		super(name, character);
		setDuration(duration);
		setValue(testModel);
	}

}