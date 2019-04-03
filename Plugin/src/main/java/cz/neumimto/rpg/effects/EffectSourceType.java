package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.api.effects.IEffectSource;

/**
 * Created by ja on 1.4.2017.
 */
public enum EffectSourceType implements IEffectSource {

	DEFAULT(true),
	RACE(false),
	GUILD(false),

	ARMOR(true),
	WEAPON(true),
	ACCESSORY(true),
	OFF_HAND(false),

	SKILL(true),
	CLASS(false),
	INTERNAL(true),
	COMMAND(true),
	EFFECT(false),
	ITEM_ACCESS_SKILL(true);
	private boolean m;


	EffectSourceType(boolean b) {
		m = b;
	}

	@Override
	public boolean multiple() {
		return m;
	}
}
