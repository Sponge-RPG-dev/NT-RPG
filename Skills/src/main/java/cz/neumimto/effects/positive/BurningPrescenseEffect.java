package cz.neumimto.effects.positive;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.model.BPModel;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.utils.Utils;

/**
 * Created by ja on 5.7.2017.
 */
@ClassGenerator.Generate(id = "name")
public class BurningPrescenseEffect extends EffectBase<BPModel> {
	public static final String name = "Burning Prescense";

	public BurningPrescenseEffect(IEffectConsumer consumer, long duration, @Inject BPModel model) {
		super(name, consumer);
		setDuration(duration);
		setValue(model);
	}


	@Override
	public IEffectContainer constructEffectContainer() {
		return new EffectContainer.UnstackableSingleInstance(this);
	}
}
