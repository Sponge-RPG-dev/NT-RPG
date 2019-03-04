package cz.neumimto.effects.decoration;

import cz.neumimto.ParticleUtils;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;

/**
 * Created by NeumimTo on 11.6.2017.
 */
@Generate(id = "name", description = "Does nothing on its own, just periodically draws particles around target")
public class FireAuraParticles extends EffectBase {

	public static final String name = "Fire particles";

	private STAGE stage;

	public FireAuraParticles(IEffectConsumer character, long duration, Void unsuded) {
		super(name, character);
		setDuration(duration);
		stage = STAGE.A;
		setPeriod(5000L);
	}

	@Override
	public void onTick(IEffect self) {
		stage = stage.process(getConsumer());
	}

	private enum STAGE {
		A {
			@Override
			public STAGE process(IEffectConsumer consumer) {
				ParticleUtils.drawSquare(consumer.getEntity().getLocation(), 2, ParticleEffect.builder().type(ParticleTypes.SMOKE).build());
				return B;
			}
		},
		B {
			@Override
			public STAGE process(IEffectConsumer consumer) {
				ParticleUtils.drawSquare(consumer.getEntity().getLocation(), 2, ParticleEffect.builder().type(ParticleTypes.LARGE_SMOKE).build());
				return C;
			}
		},
		C {
			@Override
			public STAGE process(IEffectConsumer consumer) {
				ParticleUtils.drawSquare(consumer.getEntity().getLocation(), 2, ParticleEffect.builder().type(ParticleTypes.FIRE_SMOKE).build());
				return A;
			}
		};

		public abstract STAGE process(IEffectConsumer consumer);

	}
}