package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 26.3.2018.
 */
@Singleton
public class DefaultPlayerInvHandler extends PlayerInvHandler {
    
    
    public DefaultPlayerInvHandler() {
        super("slot_order");
    }


    @Listener
    public void onInventoryEquip(ClickInventoryEvent event)

    @Override
    public void initHandler() {
        Sponge.getEventManager().registerListeners(NtRpgPlugin.GlobalScope.plugin, this);
    }

    @Override
    public void initializeCharacterInventory(IActiveCharacter character) {
        List<Integer> inventoryEquipQueue = character.getCharacterBase().getInventoryEquipSlotOrder();
        inventoryEquipQueue.stream().forEach(index -> {
            Slot query = character.getPlayer().getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(index)));
            if (checkForSlot(character, query)) {
                initializeItemStack(character, query);
            }
        });

        character.getPlayer().getInventory().slots().forEach(slot -> {
            slot.getInventoryProperty(SlotIndex.class).get().getValue();
        });
    }


    @Override
    public void onRightClick(IActiveCharacter character, int slot) {
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (!character.getDenyHotbarSlotInteractions()[slot]) {
            if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                hotbarObject.onRightClick(character);
            }
        } else if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE && hotbarObject.getHotbarObjectType() == HotbarObjectTypes.WEAPON) {
            dropItemFromMainHand(character, slot);
        }
    }

    @Override
    public void onLeftClick(IActiveCharacter character, int slot) {
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (!character.getDenyHotbarSlotInteractions()[slot]) {
            if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                hotbarObject.onLeftClick(character);
            }
        } else if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE && hotbarObject.getHotbarObjectType() == HotbarObjectTypes.WEAPON) {
            dropItemFromMainHand(character, slot);
        }
    }

    protected void dropItemFromMainHand(IActiveCharacter character, int slot) {
        character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
        Optional<ItemStack> itemInHand = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND);
        ItemStack itemStack = itemInHand.get().copy();
        character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, null);
        ItemStackUtils.dropItem(character.getPlayer(), itemStack);
    }
}
