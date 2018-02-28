package cz.neumimto.effects.negative;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;

@ClassGenerator.Generate(id = "name")
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
