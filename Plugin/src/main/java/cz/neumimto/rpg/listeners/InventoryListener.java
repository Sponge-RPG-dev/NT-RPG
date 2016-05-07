/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.listeners;

import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.inventory.HotbarObject;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.inventory.InventoryService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * Created by NeumimTo on 22.7.2015.
 */
@ResourceLoader.ListenerClass
public class InventoryListener {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private CharacterService characterService;

    @Inject
    private DamageService damageService;

    @Inject
    private Game game;

    @Listener
    public void onMouseScroll(ChangeInventoryEvent.Held event) {
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            List<SlotTransaction> transactions = event.getTransactions();
            IActiveCharacter character = characterService.getCharacter(first.get().getUniqueId());
            //todo until other events will be fully implemented this is only way how to work with hotbar
            if (!character.isStub()) {
                inventoryService.initializeHotbar(character);
            }

            for (SlotTransaction transaction : transactions) {
                Collection<SlotIndex> properties = transaction.getSlot().getProperties(SlotIndex.class);
                for (SlotIndex property : properties) {
                    Integer value = property.getValue();
                    System.out.println(value);
                }
            }
        }
    }

    @Listener
    public void onInventoryClose(InteractInventoryEvent.Close event) {
        Optional<Player> first = event.getCause().first(Player.class);
        System.out.println(event);
        if (first.isPresent()) {
            IActiveCharacter character = characterService.getCharacter(first.get().getUniqueId());
            inventoryService.initializeHotbar(character);
        }
    }

    @Listener
    public void onItemPickup(ChangeInventoryEvent.Pickup event) {
        System.out.println(event);
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            Player player = first.get();
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            if (character.isStub()) {
                return;
            }
            for (SlotTransaction slotTransaction : event.getTransactions()) {
                Inventory i = slotTransaction.getSlot();
                if (i.parent() instanceof Hotbar) {
                    Collection<SlotIndex> properties = slotTransaction.getSlot().getProperties(SlotIndex.class);
                    for (SlotIndex property : properties) {
                        Integer value = property.getValue();

                    }
                }
            }
        }
    }

    @Listener
    public void onItemDrop(DropItemEvent event) {
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {

            Player player = first.get();

            Hotbar hotbar = player.getInventory().query(Hotbar.class);
            IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
            if (character.isStub())
                return;
            HotbarObject hotbarObject = character.getHotbar()[hotbar.getSelectedSlotIndex()];
            if (hotbarObject == HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                return;
            }

            hotbarObject.onUnEquip(character);
            inventoryService.initializeHotbar( character,hotbar.getSelectedSlotIndex());
        }
    }
}
