package cz.neumimto.effects.negative;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
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
public class Bleeding extends SpongeEffectBase<Double> {

	public static final String name = "Bleeding";
	private static ParticleEffect particleEffect = ParticleEffect.builder()
			.quantity(3)
			.type(ParticleTypes.BREAK_BLOCK)
			.option(ParticleOptions.BLOCK_STATE,
					BlockState.builder()
							.blockType(BlockTypes.REDSTONE_BLOCK)
							.build())
			.build();
	private ISpongeCharacter caster;
	private SkillDamageSource source;
	private double damage;

	public Bleeding(IEffectConsumer consumer, ISpongeCharacter caster, SkillDamageSource source, double damage, long period, long duration) {
		super(name, consumer);
		this.caster = caster;
		setDuration(duration);
		setPeriod(period);
		this.damage = damage;
		this.source = source;
	}

	@Override
	public void onTick(IEffect self) {
		ISpongeEntity consumer = (ISpongeEntity) getConsumer();
		if (Utils.canDamage(caster, consumer.getEntity())) {
			consumer.getEntity().damage(damage, source);
			Location<World> location = consumer.getEntity().getLocation();
			location.getExtent().spawnParticles(particleEffect, location.getPosition());
		}
	}
}
