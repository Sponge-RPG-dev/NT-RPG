package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.CannotUseItemReson;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.items.types.CustomItem;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 26.3.2018.
 */
@Singleton
public class DefaultPlayerInvHandler extends PlayerInvHandler {
    
    
    public DefaultPlayerInvHandler() {
        super("persisted_slot_order");
    }

    @Override
    public void initHandler() {
        Sponge.getEventManager().registerListeners(NtRpgPlugin.GlobalScope.plugin, this);
    }

    @Override
    public void initializeCharacterInventory(IActiveCharacter character) {
        List<Integer> inventoryEquipQueue = character.getCharacterBase().getInventoryEquipSlotOrder();
        inventoryEquipQueue.forEach(index -> {
            Slot query = character.getPlayer().getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(index)));
            if (checkForSlot(character, query)) {
                initializeItemStack(character, query);
            }
        });

        character.getSlotsCannotBeEquiped().clear();

        Iterator<Integer> iterator = character.getCharacterBase().getInventoryEquipSlotOrder().iterator();

        Integer slot = null;
        while (iterator.hasNext()) {
            slot = iterator.next();
            IEffectSource slotSource = inventoryService().getEffectSourceBySlotId(slot);
            if (slotSource == null) {
                iterator.remove();
                continue;
            }
            Inventory query = character.getPlayer().getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot)));
            if (checkForSlot(character, query)) {
                initializeItemStack(character, query);
                updateEquipOrder(character, slot);
            } else {
                character.getSlotsCannotBeEquiped().add(slot);
            }
        }

        adjustDamage(character);
    }



    @Override
    public void onRightClick(IActiveCharacter character, int slot) {
        onHandInteract(character, slot);
    }

    @Override
    public void onLeftClick(IActiveCharacter character, int slot) {
        onHandInteract(character, slot);
    }

    protected void onHandInteract(IActiveCharacter character, int slot) {
        int mainHandSlotId = character.getMainHandSlotId();

        if (slot != mainHandSlotId) {
            Inventory query = character.getPlayer().getInventory();
            Inventory theslot = query.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot)));
            Optional<ItemStack> peek = theslot.peek();
            if (!peek.isPresent()) {
                CustomItem customItem = character.getMainHand();
                if (customItem != null) {
                    deInitializeItemStack(character, theslot);
                }
                character.setMainHand(null, -1);
                return;
            }
            ItemStack itemStack = peek.get();
            RPGItemType fromItemStack = itemService().getFromItemStack(itemStack);
            if (fromItemStack == null)
                return;
            CannotUseItemReson cannotUseItemReson = inventoryService().canUse(itemStack, character, fromItemStack);
            if (cannotUseItemReson != CannotUseItemReson.OK) {
                CustomItem customItem = character.getMainHand();
                if (customItem != null) {
                    deInitializeItemStack(character, theslot);
                    character.setMainHand(null, -1);
                }
                Gui.sendCannotUseItemNotification(character, itemStack, cannotUseItemReson);
            } else {
                CustomItem customItem = initializeItemStack(character, theslot);
                character.setMainHand(customItem, slot);
            }
        }
        adjustDamage(character);
    }

    protected void updateEquipOrder(IActiveCharacter character, int curent) {
        List<Integer> inventoryEquipSlotOrder = character.getCharacterBase().getInventoryEquipSlotOrder();
        Iterator<Integer> iterator = inventoryEquipSlotOrder.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            if (next.equals(curent)) {
                iterator.remove();
                break;
            }
        }
        inventoryEquipSlotOrder.add(curent);
    }


    protected void adjustDamage(IActiveCharacter character) {
        damageService().recalculateCharacterWeaponDamage(character);
    }
}
