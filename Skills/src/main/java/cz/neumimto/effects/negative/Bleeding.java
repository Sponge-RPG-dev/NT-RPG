package cz.neumimto.effects.negative;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by NeumimTo on 5.8.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class Bleeding extends EffectBase<Double> {

	public static final String name = "Bleeding";
	private static ParticleEffect particleEffect = ParticleEffect.builder()
			.quantity(3)
			.type(ParticleTypes.BREAK_BLOCK)
			.option(ParticleOptions.BLOCK_STATE,
					BlockState.builder()
							.blockType(BlockTypes.REDSTONE_BLOCK)
							.build())
			.build();
	private IActiveCharacter caster;
	private SkillDamageSource source;
	private double damage;

	public Bleeding(IEffectConsumer consumer, IActiveCharacter caster, SkillDamageSource source, double damage, long period, long duration) {
		super(name, consumer);
		this.caster = caster;
		setDuration(duration);
		setPeriod(period);
		this.damage = damage;
		this.source = source;
	}

	@Override
	public void onTick(IEffect self) {
		if (Utils.canDamage(caster, getConsumer().getEntity())) {
			getConsumer().getEntity().damage(damage, source);
			Location<World> location = getConsumer().getEntity().getLocation();
			location.getExtent().spawnParticles(particleEffect, location.getPosition());
		}
	}
}
