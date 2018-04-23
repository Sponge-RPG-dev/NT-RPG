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

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

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

	@Listener
	public void onInventoryClose(InteractInventoryEvent.Close event, @First(typeFilter = {Player.class}) Player player) {
		//todo
	}

    @Listener
    public void onItemDrop(DropItemEvent event, @Root Entity entity) {
        if (entity.getType() != EntityTypes.PLAYER)
            return;
        IActiveCharacter character = characterService.getCharacter(entity.getUniqueId());
        if (character.isStub())
            return;

		//todo
	}

	@Listener
	public void onArmorInteract(InteractItemEvent event, @First(typeFilter = Player.class) Player player) {
		IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
		if (ItemStackUtils.any_armor.contains(event.getItemStack().getType())) {
			event.setCancelled(true);
		}
		/*
		ItemStack is = event.getItemStack().createStack();
		CannotUseItemReson reason = inventoryService.canWear(is, character);
		if (reason != CannotUseItemReson.OK) {
			Gui.sendCannotUseItemNotification(character, is, reason);
			event.setCancelled(true);
		}
		*/
	}

	@Listener
	@Exclude({ClickInventoryEvent.Drop.class, ClickInventoryEvent.NumberPress.class})
	public void onClick(ClickInventoryEvent event, @Root Player player) {
		List<SlotTransaction> transactions = event.getTransactions();
		for (SlotTransaction transaction : transactions) {
			Optional<SlotIndex> inventoryProperty = transaction.getSlot().getInventoryProperty(SlotIndex.class);
			if (inventoryProperty.isPresent()) {

				boolean cancell = inventoryService.processSlotInteraction(transaction.getSlot(), player);
				event.setCancelled(cancell);
			}
		}
	}
}
