package cz.neumimto.rpg.inventory;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.inventory.items.types.CustomItem;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import javax.inject.Singleton;
import java.util.HashMap;


//Just in case somebody would like to inherit from CustomItem class
@Singleton
public class CustomItemFactory {

	private static CustomItemBuilder builder;

	private static InventoryService inventoryService;

	private static ItemService itemService;

	public static CustomItem createCustomItem(ItemStack is, Slot value) {
		Inventory slot = value.transform();
		SlotIndex index = slot.getInventoryProperty(SlotIndex.class).get();
		return builder.create(is, slot.parent(), index.getValue());
	}

	public static CustomItem createCustomItemForHandSlot(ItemStack is, HandType type) {
		return builder.createForHandSlot(is, type);
	}

	public void initBuilder() {
		builder = new CustomItemBuilder();
		inventoryService = NtRpgPlugin.GlobalScope.inventorySerivce;
		itemService = NtRpgPlugin.GlobalScope.itemService;
		//I should create InjectStatic/Inject lazy some day.
	}

	public static class CustomItemBuilder {

		public CustomItem create(ItemStack itemStack, Inventory parent, Integer value) {

			CustomItem customItem = new CustomItem(itemStack, inventoryService.getEffectSourceBySlotId(parent.getClass(), value),
					itemService.getFromItemStack(itemStack));
			if (itemStack.getType() == ItemTypes.NONE) {
				customItem.setEffects(new HashMap<>());
				customItem.setLevel(0);
			} else {
				customItem.setEffects(inventoryService.getItemEffects(itemStack));
				customItem.setLevel(inventoryService.getItemLevel(itemStack));
			}
			return customItem;
		}

		public CustomItem createForHandSlot(ItemStack itemStack, HandType handType) {

			CustomItem customItem = new CustomItem(itemStack, handType == HandTypes.OFF_HAND ? SlotEffectSource.OFF_HAND : SlotEffectSource
					.MAIN_HAND,
					itemService.getFromItemStack(itemStack));
			if (itemStack.getType() == ItemTypes.NONE) {
				customItem.setEffects(new HashMap<>());
				customItem.setLevel(0);
			} else {
				customItem.setEffects(inventoryService.getItemEffects(itemStack));
				customItem.setLevel(inventoryService.getItemLevel(itemStack));
			}
			return customItem;
		}

	}


}
