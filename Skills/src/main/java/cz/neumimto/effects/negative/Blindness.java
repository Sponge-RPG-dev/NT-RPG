package cz.neumimto.effects.negative;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.*;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 9.7.2017.
 */
@ClassGenerator.Generate(id = "name")
public class Blindness extends EffectBase<Long> implements IEffectContainer<Long, Blindness> {

	public static final String name = "Blindness";

	public Blindness(IEffectConsumer consumer, long duration, String value) {
		this(consumer, duration);
		setValue(duration);
		setDuration(duration);
		getPotions().add(getEffect());
	}

	public Blindness(IEffectConsumer consumer, long duration) {
		super(name, consumer);
	}


	@Override
	public Set<Blindness> getEffects() {
		return new HashSet<>(Arrays.asList(this));
	}

	@Override
	public Long getStackedValue() {
		return getDuration();
	}

	@Override
	public void setStackedValue(Long aLong) {
		setDuration(aLong);
	}

	@Override
	public void stackEffect(Blindness blindness, IEffectSourceProvider effectSourceProvider) {
		setStackedValue(getStackedValue() + blindness.getStackedValue());
	}

	private PotionEffect getEffect() {
		return PotionEffect.builder()
				.potionType(PotionEffectTypes.BLINDNESS)
				.particles(false)
				.duration((int) (20 * getStackedValue() / 1000))
				.build(); //miliseconds to minecraft ticks
	}
}
