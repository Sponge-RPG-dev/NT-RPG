package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.effects.IEffectSourceProvider;
import org.spongepowered.api.item.ItemType;

/**
 * Created by fs on 5.10.17.
 */
public class ConfigRPGItemType extends RPGItemType implements Comparable<ConfigRPGItemType> {

	public double damage;
	public IEffectSourceProvider fromParent;

	public ConfigRPGItemType(ItemType type1, String name, IEffectSourceProvider iEffectSource, double damage) {
		super(type1,name);
		this.fromParent = iEffectSource;
		this.damage = damage;
	}

	public ConfigRPGItemType(ItemType itemType, String displayName, double damage) {
		super(itemType, displayName);
		this.damage = damage;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public IEffectSourceProvider getSource() {
		return fromParent;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ConfigRPGItemType that = (ConfigRPGItemType) o;

		return fromParent.equals(that.fromParent) && getItemType() == that.getItemType();
	}

	@Override
	public int hashCode() {
		return fromParent.hashCode();
	}

	@Override
	public int compareTo(ConfigRPGItemType o) {
		if (getDisplayName() == null) {
			return -1;
		}
		return (int) (Integer.MAX_VALUE - o.damage);
	}
}
