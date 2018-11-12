package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.effects.IEffectSourceProvider;

/**
 * Created by fs on 5.10.17.
 */
public final class ConfigRPGItemType implements Comparable<ConfigRPGItemType> {

	public final double damage;
	public final IEffectSourceProvider fromParent;
	public final RPGItemType rpgItemType;

	public ConfigRPGItemType(RPGItemType type, IEffectSourceProvider iEffectSource, double damage) {
		this.fromParent = iEffectSource;
		this.damage = damage;
		this.rpgItemType = type;
	}

	public double getDamage() {
		return damage;
	}

	public IEffectSourceProvider getSource() {
		return fromParent;
	}

	public RPGItemType getRpgItemType() {
		return rpgItemType;
	}

	@Override
	public int compareTo(ConfigRPGItemType o) {
		double i = o.damage - damage;
		if (i == 0) {
			i = 1;
		}
		return (int) i;
	}
}
