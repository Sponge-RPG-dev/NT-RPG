package cz.neumimto.rpg.inventory;

import org.spongepowered.api.item.ItemType;

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
}
