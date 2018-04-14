package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.List;

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

    }

    @Override
    public void onLeftClick(IActiveCharacter character, int slot) {

    }

}
