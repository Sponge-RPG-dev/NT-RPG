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
}
