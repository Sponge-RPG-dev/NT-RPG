package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;

/**
 * Created by NeumimTo on 9.7.2017.
 */
@ClassGenerator.Generate(id = "name", description = "A component which allows player to interact with brewning stand block")
public class AlchemyEffect extends EffectBase {

	public static final String name = "Alchemy";

	public AlchemyEffect(IEffectConsumer consumer, long duration, String value) {
		super(name, consumer);
		setDuration(duration);
	}
}
