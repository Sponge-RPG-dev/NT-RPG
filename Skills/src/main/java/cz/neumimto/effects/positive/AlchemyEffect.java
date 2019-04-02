package cz.neumimto.effects.positive;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 9.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "A component which allows player to interact with brewning stand block")
public class AlchemyEffect extends EffectBase {

	public static final String name = "Alchemy";

	public AlchemyEffect(IEffectConsumer consumer, long duration) {
		super(name, consumer);
		setDuration(duration);
	}
}
