package cz.neumimto.effects;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@ClassGenerator.Generate(id = "name")
public class ResoluteTechniqueEffect extends EffectBase {

	public static final String name = "Resolute Technique";

	public ResoluteTechniqueEffect(IEffectConsumer consumer, long duration, String value) {
		super(name, consumer);
		setDuration(duration);
		setStackable(true, null);
	}
}
