package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.inventory.CannotUseItemReson;
import cz.neumimto.rpg.inventory.HotbarObject;
import cz.neumimto.rpg.inventory.HotbarObjectTypes;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.Weapon;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;

import java.util.Optional;

/**
 * Created by NeumimTo on 26.3.2018.
 */
public class DefaultSlotIterator extends SlotIterator {

    @Inject
    private InventoryService inventoryService;

    public DefaultSlotIterator() {
        super("slot_order");
    }

    @Override
    public void initializeHotbar(IActiveCharacter character) {
        Hotbar hotbar = character.getPlayer().getInventory().query(Hotbar.class);
        int slot = 0;
        for (Inventory inventory : hotbar) {
            initializeHotbar(character, slot, (Slot) inventory, hotbar);
            slot++;
        }
    }

    //TODO second hand
    public void initializeHotbar(IActiveCharacter character, int slot, Slot s, Hotbar hotbar) {
        int selectedSlotIndex = hotbar.getSelectedSlotIndex();
        //very stupid but fast
        Optional<ItemStack> peek = s.peek();

        //unequip
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (hotbarObject != null) {
            hotbarObject.onUnEquip(character);
        }

        //parse slot
        if (peek.isPresent()) {
            ItemStack itemStack1 = peek.get();
            HotbarObject hotbarObject0 = inventoryService.getHotbarObject(character, itemStack1);

            if (hotbarObject0 != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                hotbarObject0.setSlot(slot);
                character.getHotbar()[slot] = hotbarObject0;

                CannotUseItemReson reason = inventoryService.canUse(itemStack1, character);
                //cannot use
                if (reason != CannotUseItemReson.OK) {
                    character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
                    return;
                }

                //charm is active anywhere in the hotbar
                if (hotbarObject0.getHotbarObjectType() == HotbarObjectTypes.CHARM) {
                    hotbarObject0.onEquip(character);
                } else if (hotbarObject0.getHotbarObjectType() == HotbarObjectTypes.WEAPON && slot == selectedSlotIndex) {
                    //weapon active only if item is in hand
                    hotbarObject0.onRightClick(character); //simulate player interaction to equip the weapon
                    ((Weapon) hotbarObject0).setCurrent(true);
                }

            } else {
                //consumable/non weapon item
                character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
            }

        } else {
            //empty slot
            character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
        }
    }

    @Override
    public void initializeArmor(IActiveCharacter character) {

    }
}
