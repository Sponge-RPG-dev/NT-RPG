package cz.neumimto.effects.positive;

import cz.neumimto.model.BurningpresenseModel;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.*;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.utils.Utils;
import cz.neumimto.skills.BurningPrescense;

/**
 * Created by ja on 5.7.2017.
 */
@ClassGenerator.Generate(id = "name")
public class BurningPrescenseEffect extends EffectBase<BurningpresenseModel>{
	public static final String name = "Burning Prescense";

	public BurningPrescenseEffect(IEffectConsumer consumer, long duration, String value) {
		super(name, consumer);
		setDuration(duration);
		BurningpresenseModel model = new BurningpresenseModel();
		String[] split = value.split(", ");
		if (value.length() > 0) {
			model.damage = Float.parseFloat(Utils.extractNumber(split[0]));
			model.radius = 3;
			model.period = 3000;
			if (value.length() > 1) {
				model.radius = Float.parseFloat(Utils.extractNumber(split[1]));
				if (value.length() >= 2) {
					model.period = Long.parseLong(Utils.extractNumber(split[1])) * 100;
				}
			}
		}
	}


	public BurningPrescenseEffect(IEffectConsumer consumer, long duration, BurningpresenseModel model) {
		super(name, consumer);
		setDuration(duration);
		setValue(model);
	}


	@Override
	public IEffectContainer constructEffectContainer() {
		return new EffectContainer.UnstackableSingleInstance(this);
	}
}
