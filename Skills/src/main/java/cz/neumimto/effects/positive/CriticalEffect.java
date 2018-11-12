package cz.neumimto.effects.positive;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.model.CriticalEffectModel;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "% chance to deal increased damage while attacking")
public class CriticalEffect extends EffectBase<CriticalEffectModel> {

	public static final String name = "Critical";

	public CriticalEffect(IEffectConsumer consumer, long duration, @Inject CriticalEffectModel model) {
		super(name, consumer);
		setValue(model);
		setStackable(true, null);
		setDuration(duration);
	}
}
