package cz.neumimto.effects.decoration;

import cz.neumimto.ParticleUtils;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;

/**
 * Created by NeumimTo on 11.6.2017.
 */
@Generate(id = "name", description = "Does nothing on its own, just periodically draws particles around target")
public class FireAuraParticles extends SpongeEffectBase {

	public static final String name = "Fire particles";

	private STAGE stage;

	public FireAuraParticles(IEffectConsumer character, long duration) {
		super(name, character);
		setDuration(duration);
		stage = STAGE.A;
		setPeriod(5000L);
	}

	@Override
	public void onTick(IEffect self) {
		stage = stage.process((ISpongeEntity) getConsumer());
	}

	private enum STAGE {
		A {
			@Override
			public STAGE process(ISpongeEntity consumer) {
				ParticleUtils.drawSquare(consumer.getEntity().getLocation(), 2, ParticleEffect.builder().type(ParticleTypes.SMOKE).build());
				return B;
			}
		},
		B {
			@Override
			public STAGE process(ISpongeEntity consumer) {
				ParticleUtils.drawSquare(consumer.getEntity().getLocation(), 2, ParticleEffect.builder().type(ParticleTypes.LARGE_SMOKE).build());
				return C;
			}
		},
		C {
			@Override
			public STAGE process(ISpongeEntity consumer) {
				ParticleUtils.drawSquare(consumer.getEntity().getLocation(), 2, ParticleEffect.builder().type(ParticleTypes.FIRE_SMOKE).build());
				return A;
			}
		};

		public abstract STAGE process(ISpongeEntity consumer);

	}
}