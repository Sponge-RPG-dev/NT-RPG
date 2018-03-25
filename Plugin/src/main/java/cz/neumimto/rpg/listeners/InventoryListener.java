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
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.CannotUseItemReson;
import cz.neumimto.rpg.inventory.HotbarObject;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;


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
	public void onInventoryClose(InteractInventoryEvent.Close event, @First(typeFilter = {Player.class}) Player player) {
		IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
		if (player.get(Keys.GAME_MODE).get() == GameModes.CREATIVE)
			return;
		inventoryService.initializeHotbar(character);
		inventoryService.initializeArmor(character);
		damageService.recalculateCharacterWeaponDamage(character);
	}

	/* Tempoar */
	@Listener
	public void onInventoryOpen(InteractInventoryEvent.Close event, @First(typeFilter = {Player.class}) Player player) {
		IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
		if (character == null)
			return;
		if (!character.isStub()) {
			character.setOpenInventory(false);
		}

	}

	@Listener
	public void onInventoryOpen(InteractInventoryEvent.Open event, @First(typeFilter = {Player.class}) Player player) {
		IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
		if (character == null)
			return;
		if (!character.isStub()) {
			character.setOpenInventory(true);
		}
	}


	@Listener
	public void onItemPickup(ChangeInventoryEvent.Pickup event, @First(typeFilter = {Player.class}) Player player) {

		IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
		if (character.isStub()) {
			return;
		}
		Hotbar hotbar = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
		for (SlotTransaction slotTransaction : event.getTransactions()) {
			Slot i = slotTransaction.getSlot();
			if (hotbar.containsInventory(i)) {
				inventoryService.initializeHotbar(character);
			}
		}
	}


    @Listener
    public void onItemDrop(DropItemEvent event, @Root Entity entity) {
        if (entity.getType() != EntityTypes.PLAYER)
            return;
        IActiveCharacter character = characterService.getCharacter(entity.getUniqueId());
        if (character.isStub())
            return;
        if (character.hasOpenInventory()) {
            return;
        }

		Hotbar hotbar = character.getPlayer().getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
		HotbarObject hotbarObject = character.getHotbar()[hotbar.getSelectedSlotIndex()];
		if (hotbarObject == HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
			return;
		}
		inventoryService.initializeHotbar(character);
	}

	@Listener
	public void onArmorInteract(InteractItemEvent event, @First(typeFilter = Player.class) Player player) {
		IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
		ItemStack is = event.getItemStack().createStack();
		CannotUseItemReson reason = inventoryService.canWear(is, character);
		if (reason != CannotUseItemReson.OK) {
			Gui.sendCannotUseItemNotification(character, is, reason);
			event.setCancelled(true);
		}
	}
}
