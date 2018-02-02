package cz.neumimto.effects.positive;

import cz.neumimto.core.ioc.Inject;
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

	public CriticalEffect(IActiveCharacter consumer, long duration, @Inject CriticalEffectModel model) {
		super(name, consumer);
		setValue(model);
		setStackable(true, null);
		setDuration(duration);
	}
}
