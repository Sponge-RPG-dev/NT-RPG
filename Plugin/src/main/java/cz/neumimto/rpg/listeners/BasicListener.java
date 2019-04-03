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

import com.flowpowered.math.vector.Vector3i;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.entities.IEntityType;
import cz.neumimto.rpg.exp.ExperienceService;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExperienceSources;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.ItemStackUtils;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.Fish;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.FishHook;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.FishingEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 12.2.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
public class BasicListener {

	@Inject
	private CharacterService characterService;

	@Inject
	private InventoryService inventoryService;

	@Inject
	private EntityService entityService;

	@Inject
	private ExperienceService experienceService;

	@Listener(order = Order.LATE)
	public void onAttack(InteractEntityEvent.Primary event) {
		if (!Utils.isLivingEntity(event.getTargetEntity())) {
			return;
		}

		Optional<Player> first = event.getCause().first(Player.class);
		IActiveCharacter character = null;
		if (first.isPresent()) {
			character = characterService.getCharacter(first.get().getUniqueId());
			if (character.isStub()) {
				return;
			}
			Hotbar h = character.getPlayer().getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
			int slotIndex = h.getSelectedSlotIndex();
			inventoryService.onLeftClick(character, h.getSelectedSlotIndex(), h.getSlot(new SlotIndex(slotIndex)).get());
		}

		IEntity entity = entityService.get(event.getTargetEntity());

		if (entity.getType() == IEntityType.CHARACTER) {
			IActiveCharacter target = characterService.getCharacter(event.getTargetEntity().getUniqueId());
			if (target.isStub() && !pluginConfig.ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS) {
				event.setCancelled(true);
				return;
			}
			if (first.isPresent()) {
				if (character.getParty() == target.getParty() && !character.getParty().isFriendlyfire()) {
					event.setCancelled(true);
				}
			}
		}
	}

	@Listener
	public void onRightClick(InteractEntityEvent.Secondary event, @First Player pl) {
		Optional<ItemStack> itemInHand = pl.getItemInHand(HandTypes.MAIN_HAND);
		if (itemInHand.isPresent()) {
			ItemStack itemStack = itemInHand.get();
			if (ItemStackUtils.any_armor.contains(itemStack.getType())) {
				event.setCancelled(true); //restrict armor equip on rightclick
				return;
			} else {
				IActiveCharacter character = characterService.getCharacter(pl.getUniqueId());
				if (character.isStub()) {
					return;
				}
				Hotbar h = pl.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
				int slotIndex = h.getSelectedSlotIndex();
				inventoryService.onRightClick(character, 0, h.getSlot(new SlotIndex(slotIndex)).get());
			}
		}
	}

	@Listener
	public void onBlockClick(InteractBlockEvent.Primary event, @First Player pl) {
		IActiveCharacter character = characterService.getCharacter(pl.getUniqueId());
		if (character.isStub()) {
			return;
		}
		Hotbar h = pl.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
		int slotIndex = h.getSelectedSlotIndex();
		inventoryService.onLeftClick(character, slotIndex, h.getSlot(new SlotIndex(slotIndex)).get());
	}

	@Listener
	public void onBlockRightClick(InteractBlockEvent.Secondary event, @First Player pl) {
		IActiveCharacter character = characterService.getCharacter(pl.getUniqueId());
		Optional<ItemStack> itemInHand = pl.getItemInHand(HandTypes.MAIN_HAND);
		if (itemInHand.isPresent() && ItemStackUtils.any_armor.contains(itemInHand.get().getType())) {
			event.setCancelled(true); //restrict armor equip on rightclick
			return;
		}
		if (character.isStub()) {
			return;
		}
		Hotbar h = pl.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
		int slotIndex = h.getSelectedSlotIndex();
		inventoryService.onRightClick(character, slotIndex, h.getSlot(new SlotIndex(slotIndex)).get());
	}

	@Listener
	public void onRespawn(RespawnPlayerEvent event) {
		Entity type = event.getTargetEntity();
		if (type.getType() == EntityTypes.PLAYER) {
			IActiveCharacter character = characterService.getCharacter(type.getUniqueId());
			if (character.isStub()) {
				return;
			}
			characterService.respawnCharacter(character);
		}
	}

	@Listener(order = Order.POST)
	public void onBlockBreak(ChangeBlockEvent.Break event, @First Player player) {
		IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockType type = transaction.getOriginal().getState().getType();
			Double d = experienceService.getMinningExperiences(type);
			if (d != null) {
				characterService.addExperiences(character, d, ExperienceSources.MINING);
				return;
			}
			d = experienceService.getFarmingExperiences(type);
			if (d != null) {
				characterService.addExperiences(character, d, ExperienceSources.FARMING);
				return;
			}
			d = experienceService.getLoggingExperiences(type);
			if (d != null) {
				characterService.addExperiences(character, d, ExperienceSources.LOGGING);
				return;
			}
		}
	}

	@Listener(order = Order.POST)
	@Exclude({FishingEvent.Start.class, FishingEvent.HookEntity.class})
	public void onFishCatch(FishingEvent.Stop event, @First Player player) {
		FishHook fishHook = event.getFishHook();
		List<Transaction<ItemStackSnapshot>> transactions = event.getTransactions();
		if (transactions.isEmpty()) {
			return;
		}
		Transaction<ItemStackSnapshot> transaction = transactions.get(0);
		ItemStackSnapshot aFinal = transaction.getFinal();
		Optional<Fish> ofish = aFinal.get(Keys.FISH_TYPE);

		if (ofish.isPresent()) {
			Fish fish = ofish.get();
			Double d = experienceService.getFishingExperience(fish);
			if (d == null) {
				return;
			}
			Location<World> location = fishHook.getLocation();
			BlockType blockType = location.getBlockType();
			if (blockType != BlockTypes.WATER) {
				location = location.add(0, -1, 0);
				blockType = location.getBlockType();
				if (blockType != BlockTypes.WATER) {
					return;
				}
			}
			IActiveCharacter character = characterService.getCharacter(player);
			characterService.addExperiences(character, d, ExperienceSources.FISHING);
		}


//		IActiveCharacter character = characterService.getTarget(player.getUniqueId());

	//	characterService.addExperiences(character, 10, ExperienceSources.FISHING);

	}

	//TODO sponge bug as of 7.1-BETA-15
    /*
    @Listener
    public void onItemForge(ClickInventoryEvent event, @First Player player) {
        Container i = event.getTargetInventory();
        if (i.getArchetype() == InventoryArchetypes.ANVIL) {
            if (i instanceof BlockCarrier) {
                BlockCarrier b = (BlockCarrier) i;
                //on item insert
                if (isSocketingViable(b.getLocation())) {
                    Optional<ItemStack> peek = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(0))).peek();
                    Optional<ItemStack> peek1 = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(1))).peek();
                    if (peek.isPresent() && peek1.isPresent()) {

                        ItemStack rune = peek1.get();
                        if (!rwService.isRune(rune)) {
                            event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(2))).set(ItemStack.empty());
                            return;
                        }
                        ItemStack itemStack = peek.get();
                        ItemStack copy = itemStack.copy();
                        ItemUpgradeTransactionResult result = rwService.insertToNextEmptySocket(copy, rune);
                        if (result != ItemUpgradeTransactionResult.OK) {
                            event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(2))).set(ItemStack.empty());
                        } else {
                            event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(2))).set(copy);
                        }
                    } else {
                        event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(2))).set(ItemStack.empty());
                    }
                }
                for (SlotTransaction slotTransaction : event.getTransactions()) {
                    Slot slot = slotTransaction.getSlot();
                }
            }
        }
    }
    */


	private boolean isSocketingViable(Location<World> location) {
		World extent = location.getExtent();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				Vector3i vector3i = Vector3i.from(location.getBlockX() + i, location.getBlockY(), location.getBlockZ() + j);
				if (extent.getBlock(vector3i).getType() != BlockTypes.REDSTONE_WIRE) {
					return false;
				}
			}
		}
		return true;
	}



}
