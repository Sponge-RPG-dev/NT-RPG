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

	public RPGItemType(ItemType itemType, String itemName, WeaponClass weaponClass) {
		this.itemType = itemType;
		this.displayName = itemName;
		this.weaponClass = weaponClass;
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
		return itemType.hashCode() * 77 * 31;
	}
}
