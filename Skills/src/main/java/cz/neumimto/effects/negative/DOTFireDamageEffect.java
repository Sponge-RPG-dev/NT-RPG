package cz.neumimto.effects.negative;

import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;

@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect which applies Dame Over Time debuff to the target.")
public class DOTFireDamageEffect extends SpongeEffectBase {

	public static final String name = "Fire damage over time";

	private double damage;

	public DOTFireDamageEffect(IEffectConsumer consumer, long duration, DotDamageEffectModel model) {
		super(name, consumer);
		setDuration(duration);
		setPeriod(model.period);
		this.damage = model.damage;
	}

	@Override
	public void onTick(IEffect self) {
		((ISpongeEntity)getConsumer()).getEntity().damage(damage, DamageSources.FIRE_TICK);
	}
}
