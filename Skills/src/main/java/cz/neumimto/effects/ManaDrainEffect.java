package cz.neumimto.effects;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect whichgives manadrain to the target")
public class ManaDrainEffect extends EffectBase<Float> {

	public static final String name = "Mana Drain";

	public ManaDrainEffect(IEffectConsumer character, long duration, float value) {
		super(name, character);
		setDuration(duration);
		setValue(value);
	}

}