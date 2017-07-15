package cz.neumimto.effects.positive;

import cz.neumimto.model.BashModel;
import cz.neumimto.model.CriticalEffectModel;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@ClassGenerator.Generate(id = "name")
public class CriticalEffect extends EffectBase<CriticalEffectModel> {

	public static final String name = "Critical";

	public CriticalEffect(IActiveCharacter consumer,long duration, CriticalEffectModel model) {
		super(name, consumer);
		setValue(model);
		setStackable(true, null);
		setDuration(duration);
	}

	public CriticalEffect(IEffectConsumer consumer, long duration, String value) {
		super(name, consumer);
		CriticalEffectModel model = new CriticalEffectModel();

		String[] split = value.split(", ");
		if (split.length > 0) {
			model.chance = Integer.parseInt(Utils.extractNumber(split[0]));
			model.mult = 2;
			if (split.length > 1) {
				model.mult = Float.parseFloat(Utils.extractNumber(split[1]));
			}
		}
		setValue(model);
		setDuration(duration);
		setStackable(true, null);
	}
}
