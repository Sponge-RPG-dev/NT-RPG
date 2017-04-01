package cz.neumimto.rpg.effects;

/**
 * Created by ja on 1.4.2017.
 */
public enum EffectSourceType implements IEffectSource {

	DEFAULT(true),
	RACE(false),
	GUILD(false),
	LEGGINGS(false),
	CHESTPLATE(false),
	BOOTS(false),
	HELMET(false),
	WEAPON(false),
	SKILL(true),
	OFF_HAND(false),
	CLASS(false),
	CHARM(true),
	INTERNAL(true),
	COMMAND(true);

	private boolean m;


	EffectSourceType(boolean b) {
		m = b;
	}

	@Override
	public boolean multiple() {
		return m;
	}
}
