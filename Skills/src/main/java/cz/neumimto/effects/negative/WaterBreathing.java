package cz.neumimto.effects.negative;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;

/**
 * Created by NeumimTo on 3.6.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "An effect which gives water_breathing potion effect to the target")
public class WaterBreathing extends EffectBase<Object> {

	public static final String name = "Water Breathing";

	public WaterBreathing(IEffectConsumer consumer, long duration, Void nll) {
		super(name, consumer);
		setDuration(duration);
		setPeriod(2000L);
		super.getPotions().add(PotionEffect.builder().potionType(PotionEffectTypes.WATER_BREATHING).amplifier(1).duration(2500).build());
	}

	@Override
	public void onTick() {
		super.onApply();
	}

	@Override
	public void tickCountIncrement() {
	}


}
