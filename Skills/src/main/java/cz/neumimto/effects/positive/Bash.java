package cz.neumimto.effects.positive;

import cz.neumimto.model.BashModel;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.utils.Utils;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@ClassGenerator.Generate(id = "name")
public class Bash extends EffectBase<BashModel> {

	public static final String name = "Bash";

	public Bash(IEffectConsumer consumer, long duration, String value) {
		super(name, consumer);
		BashModel model = new BashModel();

		String[] split = value.split(", ");
		if (split.length > 0) {
			model.chance = Integer.parseInt(Utils.extractNumber(split[0]));
			model.damage = 0;
			model.cooldown = 2000;
			model.stunDuration = 500;
			if (split.length > 1) {
				model.damage = Double.parseDouble(Utils.extractNumber(split[1]));
				if (split.length > 2) {
					model.stunDuration = Long.parseLong(Utils.extractNumber(split[2])) * 1000;
					if (split.length > 3) {
						model.cooldown = Long.parseLong(Utils.extractNumber(split[3])) * 100;
					}
				}
			}
		}
		setValue(model);
		setDuration(duration);
		setStackable(true, null);
	}

	public Bash(IEffectConsumer consumer, long duration, BashModel value) {
		super(name, consumer);
		setValue(value);
		setDuration(duration);
	}


	@Override
	public IEffectContainer constructEffectContainer() {
		return new EffectContainer.UnstackableSingleInstance(this);
	}
}
