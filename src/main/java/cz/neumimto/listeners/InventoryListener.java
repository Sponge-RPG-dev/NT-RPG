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

package cz.neumimto.listeners;

import cz.neumimto.ResourceLoader;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.inventory.InventoryService;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

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
    private Game game;

    @Listener
    public void onInventoryClose(InteractInventoryEvent.Close event) {
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            IActiveCharacter character = characterService.getCharacter(first.get().getUniqueId());
            inventoryService.initializeHotbar(character);
        }
    }

    @Listener
    public void onRespawn(RespawnPlayerEvent event) {
        IActiveCharacter character = characterService.getCharacter(event.getTargetEntity().getUniqueId());
        inventoryService.initializeHotbar(character);
    }

    @Listener
    public void onItemPickup(ChangeInventoryEvent.Pickup event) {
        Optional<Player> first = event.getCause().first(Player.class);
        if (first.isPresent()) {
            Player player = first.get();
                for (SlotTransaction slotTransaction : event.getTransactions()) {
                    Inventory i = slotTransaction.getSlot();
                    if (i.parent() instanceof Hotbar) {
                        SlotIndex query = i.query(SlotIndex.class);
                        Integer value = query.getValue();
                    }
                }
        }
    }

}
