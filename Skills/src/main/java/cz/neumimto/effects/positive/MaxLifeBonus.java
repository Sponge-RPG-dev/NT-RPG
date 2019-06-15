package cz.neumimto.effects.positive;

import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;


public class MaxLifeBonus extends SpongeEffectBase {

	public static final String name = "Max life";


	public MaxLifeBonus(String name, IEffectConsumer consumer) {
		super(name, consumer);
	}
}
