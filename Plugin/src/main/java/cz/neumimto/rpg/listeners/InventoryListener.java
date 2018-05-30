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
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.*;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Include;
import org.spongepowered.api.event.item.inventory.*;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.util.Tristate;

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
	private ItemService itemService;


	@Listener
	public void onInventoryClose(InteractInventoryEvent.Close event, @First(typeFilter = {Player.class}) Player player) {
		//todo
	}

    @Listener
	@IsCancelled(Tristate.FALSE)
	public void onItemDrop(DropItemEvent.Dispense event, @Root Player player) {
        if (!player.getOpenInventory().isPresent()) {
        	return;
		}

        inventoryService.processHotbarItemDispense(player);
	}

	@Listener
	public void onHotbarInteract(InteractItemEvent event, @First(typeFilter = Player.class) Player player) {
		IActiveCharacter character = characterService.getCharacter(player.getUniqueId());

		RPGItemType rpgItemType = itemService.getFromItemStack(event.getItemStack());
		if (rpgItemType != null) {
			ItemStack stack = event.getItemStack().createStack();
			CannotUseItemReason reason;
			if (rpgItemType.getWeaponClass() == WeaponClass.ARMOR) {
				reason = inventoryService.canWear(stack, character, rpgItemType);
			} else {
				reason = inventoryService.canUse(stack, character, rpgItemType);
			}
			if (reason != CannotUseItemReason.OK) {
				Gui.sendCannotUseItemNotification(character, stack, reason);
				event.setCancelled(true);
			}
		}
	}

	@Listener
	@Include({
			ClickInventoryEvent.Primary.class,
			ClickInventoryEvent.Secondary.class,
			ClickInventoryEvent.Creative.class,
			ClickInventoryEvent.Middle.class,
			ClickInventoryEvent.Shift.class
	})
	public void onClick(ClickInventoryEvent event, @Root Player player) {
		List<SlotTransaction> transactions = event.getTransactions();
		for (SlotTransaction transaction : transactions) {
			Optional<SlotIndex> inventoryProperty = transaction.getSlot().getInventoryProperty(SlotIndex.class);
			if (inventoryProperty.isPresent()) {
				boolean cancel = inventoryService.processSlotInteraction(transaction.getSlot(), player);
				if (cancel)
					event.setCancelled(cancel);
			}
		}
	}

	@Listener
	@IsCancelled(Tristate.FALSE)
	public void onDimensionTravel(MoveEntityEvent.Teleport.Portal event, @Root Player player) {
		IActiveCharacter character = characterService.getCharacter(player);
		if (!character.isStub()) {
			characterService.respawnCharacter(character, player);
		}
	}


	@Listener(order = Order.LAST)
	@IsCancelled(Tristate.FALSE)
	public void onSwapHands(ChangeInventoryEvent.SwapHand event, @Root Player player) {
		ItemStack futureMainHand = player.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
		ItemStack futureOffHand = player.getItemInHand(HandTypes.OFF_HAND).orElse(null);
		boolean cancel = inventoryService.processHotbarSwapHand(player, futureMainHand, futureOffHand);
		if (cancel) {
			event.setCancelled(true);
		}
	}
}
