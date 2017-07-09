package cz.neumimto.model;

import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 9.7.2017.
 */
public class PotionEffectModel {
	public Map<PotionEffectType, Long> potions;
	public Map<PotionEffectType, Long> cooldowns;
	public PotionEffectModel() {
		this.potions = new HashMap<>();
		cooldowns = new HashMap<>();
	}

	public void mergeWith(PotionEffectModel that) {
		that.potions.forEach((k, v) -> potions.merge(k, v, Long::min));
	}
}
