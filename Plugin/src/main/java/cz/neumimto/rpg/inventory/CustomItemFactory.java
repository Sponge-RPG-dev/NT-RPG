package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.inventory.items.types.CustomItemToRemove;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.HashMap;


//Just in case somebody would like to inherit from CustomItemToRemove class
public class CustomItemFactory {

	private static CustomItemBuilder builder;
	
	public static CustomItemToRemove createCustomItem(ItemStack is, Slot value) {
		Inventory slot = value.transform();
		SlotIndex index = slot.getInventoryProperty(SlotIndex.class).get();
		return builder.create(is, slot.parent(), index.getValue());
	}

	public static CustomItemToRemove createCustomItemForHandSlot(ItemStack is, HandType type) {
		return builder.createForHandSlot(is, type);
	}

	public void initBuilder() {
		builder = new CustomItemBuilder();
	}

	public static class CustomItemBuilder {

		public CustomItemToRemove create(ItemStack itemStack, Inventory parent, Integer value) {

			CustomItemToRemove customItem = new CustomItemToRemove(itemStack, NtRpgPlugin.GlobalScope.spongeInventoryService.getEffectSourceBySlotId(parent.getClass(), value),
					NtRpgPlugin.GlobalScope.itemService.getFromItemStack(itemStack));
			if (itemStack.getType() == ItemTypes.NONE) {
				customItem.setEffects(new HashMap<>());
				customItem.setLevel(0);
			} else {
				customItem.setEffects(NtRpgPlugin.GlobalScope.spongeInventoryService.getItemEffects(itemStack));
				customItem.setLevel(NtRpgPlugin.GlobalScope.spongeInventoryService.getItemLevel(itemStack));
			}
			return customItem;
		}

		public CustomItemToRemove createForHandSlot(ItemStack itemStack, HandType handType) {

			CustomItemToRemove customItem = new CustomItemToRemove(itemStack, handType == HandTypes.OFF_HAND ? SlotEffectSource.OFF_HAND : SlotEffectSource
					.MAIN_HAND,
					NtRpgPlugin.GlobalScope.itemService.getFromItemStack(itemStack));
			if (itemStack.getType() == ItemTypes.NONE) {
				customItem.setEffects(new HashMap<>());
				customItem.setLevel(0);
			} else {
				customItem.setEffects(NtRpgPlugin.GlobalScope.spongeInventoryService.getItemEffects(itemStack));
				customItem.setLevel(NtRpgPlugin.GlobalScope.spongeInventoryService.getItemLevel(itemStack));
			}
			return customItem;
		}

	}


}
