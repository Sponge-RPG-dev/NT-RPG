package cz.neumimto.effects.negative;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;

@ClassGenerator.Generate(id = "name")
public class DOTFireDamageEffect extends EffectBase {

	public static final String name = "Fire damage over time";

	private double damage;

	public DOTFireDamageEffect(IEffectConsumer consumer, double damage, long period, long duration) {
		super(name, consumer);
		setDuration(duration);
		setPeriod(period);
	}

	public DOTFireDamageEffect(IEffectConsumer character, long duration, String damage) {
		super(name, character);
		setDuration(duration);
		setPeriod(1000L);
		this.damage = Double.parseDouble(Utils.extractNumber(damage));
	}

	@Override
	public void onTick() {
		getConsumer().getEntity().damage(damage, DamageSources.FIRE_TICK);
	}
}
