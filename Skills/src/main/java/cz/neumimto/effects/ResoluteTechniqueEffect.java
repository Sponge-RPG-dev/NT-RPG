package cz.neumimto.effects;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.common.scripting.JsBinding;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Entity having this effect will never deal critical strike, nor may be stunned")
public class ResoluteTechniqueEffect extends EffectBase {

	public static final String name = "Resolute Technique";

	public ResoluteTechniqueEffect(IEffectConsumer consumer, long duration) {
		super(name, consumer);
		setDuration(duration);
		setStackable(true, null);
	}
}
