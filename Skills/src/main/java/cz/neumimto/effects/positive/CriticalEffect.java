package cz.neumimto.effects.positive;

import cz.neumimto.model.CriticalEffectModel;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "% chance to deal increased damage while attacking")
public class CriticalEffect extends SpongeEffectBase<CriticalEffectModel> {

	public static final String name = "Critical";

	public CriticalEffect(IEffectConsumer consumer, long duration,  CriticalEffectModel model) {
		super(name, consumer);
		setValue(model);
		setStackable(true, null);
		setDuration(duration);
	}
}
