package cz.neumimto.rpg.inventory;

import org.spongepowered.api.item.ItemType;

import java.util.Objects;

/**
 * Created by NeumimTo on 5.10.17.
 */
public class RPGItemType {

	private final ItemType itemType;

	private final String displayName;

	private final WeaponClass weaponClass;

	private final double defaultDamage;

	private int hashCode = 0;

	public RPGItemType(ItemType itemType, String itemName, WeaponClass weaponClass, double defaultDamage) {
		this.itemType = itemType;
		this.displayName = itemName;
		this.weaponClass = weaponClass;
		this.defaultDamage = defaultDamage;
		hashCode = itemType.hashCode() * 77;
		if (itemName != null) {
			hashCode += itemName.hashCode();
		}
		hashCode += defaultDamage * 11;
		if (weaponClass != null) {
			hashCode += weaponClass.hashCode();
		}
	}

	public double getDefaultDamage() {
		return defaultDamage;
	}

	public String getDisplayName() {
		return displayName;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public WeaponClass getWeaponClass() {
		return weaponClass;
	}

	public String toConfigString() {
		String s = itemType.getId() + ";" + defaultDamage;
		if (displayName != null) {
			s += ";" + displayName;
		}
		return s;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RPGItemType type = (RPGItemType) o;
		return Objects.equals(itemType, type.itemType) &&
				Objects.equals(displayName, type.displayName) &&
				Objects.equals(weaponClass, type.weaponClass);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
