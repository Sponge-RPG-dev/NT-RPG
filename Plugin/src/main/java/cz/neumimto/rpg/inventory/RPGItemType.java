package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.inventory.data.NKeys;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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
		RPGItemType itemType = new RPGItemType();
		itemType.itemType = itemStack.getType();
		Text text = itemStack.get(Keys.DISPLAY_NAME).orElse(null);
		if (text != null) {
			String name = text.toPlain();
			if (NtRpgPlugin.GlobalScope.inventorySerivce.getReservedItemNames().contains(name.toLowerCase())) {
				itemType.displayName = name;
			}
		}

		return itemType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof RPGItemType)) {
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
