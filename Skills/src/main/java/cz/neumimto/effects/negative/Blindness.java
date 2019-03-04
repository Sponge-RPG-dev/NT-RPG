package cz.neumimto.effects.negative;

import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.mechanics.RPGPotionEffect;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;

/**
 * Created by NeumimTo on 9.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Applies potion effect blindness to the target")
public class Blindness extends RPGPotionEffect {

	public static final String name = "Blindness";

	public Blindness(IEffectConsumer consumer, long duration) {
		super(name, consumer, duration, PotionEffect.builder()
				.potionType(PotionEffectTypes.BLINDNESS)
				.particles(true)
				.duration((int) (20 * duration / 1000)));
	}
}
