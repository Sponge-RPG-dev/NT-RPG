package cz.neumimto.rpg.effects.common.negative;

import cz.neumimto.rpg.effects.CommonEffectTypes;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;

/**
 * Created by NeumimTo on 17.3.2016.
 */
@Generate(id = "name", description = "An effect which silences the target. Silenced target cannot casts spells, unless the skill has"
		+ " type CAN_CAST_WHILE_SILENCED")
public class Silence extends EffectBase {

	public static String name = "Silence";

	public Silence(IEffectConsumer consumer, long duration) {
		super(name, consumer);
		setDuration(duration);
		effectTypes.add(CommonEffectTypes.SILENCE);
	}

	@Override
	public String getName() {
		return name;
	}
}
