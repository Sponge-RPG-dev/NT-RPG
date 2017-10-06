package cz.neumimto.rpg.inventory;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

/**
 * Created by NeumimTo on 5.10.17.
 */
public class RPGItemType {
	private ItemType itemType;
	private String displayName;

	public RPGItemType() {
	}

	public RPGItemType(ItemType itemType, String displayName) {
		this.itemType = itemType;
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public static RPGItemType from(ItemStack itemStack) {
		RPGItemType RPGItemType = new RPGItemType();
		RPGItemType.itemType = itemStack.getType();
		itemStack.get(Keys.DISPLAY_NAME).ifPresent(text -> RPGItemType.displayName = text.toPlain());
		return RPGItemType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof RPGItemType) {
			return false;
		}
		RPGItemType that = (RPGItemType) obj;
		if (getItemType().equals(that.getItemType())) {
			if (getDisplayName() == null && that.getDisplayName() == null)
				return true;
			if (getDisplayName() != null && getDisplayName().equalsIgnoreCase(that.getDisplayName())) {
				return true;
			}
		}
		return false;
	}
}
