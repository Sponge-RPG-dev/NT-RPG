package cz.neumimto.effects.negative;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;

@JsBinding(JsBinding.Type.CLASS)
@ClassGenerator.Generate(id = "name", description = "An effect which applies Dame Over Time debuff to the target.")
public class DOTFireDamageEffect extends EffectBase {

	public static final String name = "Fire damage over time";

	private double damage;

	public DOTFireDamageEffect(IEffectConsumer consumer, long duration, @Inject DotDamageEffectModel model) {
		super(name, consumer);
		setDuration(duration);
		setPeriod(model.period);
		this.damage = model.damage;
	}

	@Override
	public void onTick() {
		getConsumer().getEntity().damage(damage, DamageSources.FIRE_TICK);
	}
}
