package cz.neumimto.effects.positive;

import cz.neumimto.model.ShadowRunModel;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.utils.rng.XORShiftRnd;
import cz.neumimto.rpg.common.scripting.JsBinding;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An invisibility, the next attack will deal increased damage and break the invisibility")
public class ShadowRunEffect extends EffectBase<ShadowRunModel> {

	public static final String name = "ShadowRun";
	public static XORShiftRnd rnd = new XORShiftRnd();

	public ShadowRunEffect(IEffectConsumer character, long duration, ShadowRunModel shadowRunModel) {
		super(name, character);
		setStackable(false, null);
		setValue(shadowRunModel);
		setDuration(duration);
		setPeriod(20);
	}

	@Override
	public void onApply(IEffect self) {
		super.onApply(self);
		getConsumer().getEntity().offer(Keys.VANISH, true);
		getConsumer().getEntity().offer(Keys.VANISH_PREVENTS_TARGETING, true);
		getConsumer().addProperty(SpongeDefaultProperties.walk_speed, getValue().walkspeed);
		NtRpgPlugin.GlobalScope.entityService.updateWalkSpeed(getConsumer());
	}

	@Override
	public void onTick(IEffect self) {
		int i = rnd.nextInt(5);
		Location<World> location = getConsumer().getLocation();
		World extent = location.getExtent();
		extent.spawnParticles(ParticleEffect.builder()
						.quantity(i)
						.type(ParticleTypes.SMOKE)
						.build(),
				location.getPosition().add(0, 1, 0),
				5);
	}

	@Override
	public void onRemove(IEffect self) {
		super.onRemove(self);
		getConsumer().getEntity().offer(Keys.VANISH, false);
		getConsumer().getEntity().offer(Keys.VANISH_PREVENTS_TARGETING, false);
		getConsumer().addProperty(SpongeDefaultProperties.walk_speed, -getValue().walkspeed);
		NtRpgPlugin.GlobalScope.entityService.updateWalkSpeed(getConsumer());
	}
}
