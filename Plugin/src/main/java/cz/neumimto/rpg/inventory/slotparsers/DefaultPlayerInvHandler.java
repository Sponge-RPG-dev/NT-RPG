package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.CannotUseItemReason;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.WeaponClass;
import cz.neumimto.rpg.inventory.items.types.CustomItem;
import cz.neumimto.rpg.persistance.DirectAccessDao;
import cz.neumimto.rpg.persistance.model.EquipedSlot;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by NeumimTo on 26.3.2018.
 */
@Singleton
public class DefaultPlayerInvHandler extends PlayerInvHandler {

	@Inject
	private DirectAccessDao directAccessDao;

	private Set<UUID> saveQuery = new HashSet<>();

	public DefaultPlayerInvHandler() {
		super("persisted_slot_order");
	}

	@Override
	public void initHandler() {
		Sponge.getEventManager().registerListeners(NtRpgPlugin.GlobalScope.plugin, this);
	}

	@Override
	public void initializeCharacterInventory(IActiveCharacter character) {
		List<EquipedSlot> inventoryEquipQueue = character.getCharacterBase().getInventoryEquipSlotOrder();
		Player player = character.getPlayer();
		Iterator<EquipedSlot> it = inventoryEquipQueue.iterator();
		EquipedSlot slot = null;
		while (it.hasNext()) {
			slot = it.next();
			IEffectSource slotSource = inventoryService().getEffectSourceBySlotId(slot.getRuntimeInventoryClass(), slot.getSlotIndex());
			if (slotSource == null) {
				it.remove();
				continue;
			}

			OrderedInventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(slot.getRuntimeInventoryClass()));

			Optional<Slot> slot1 = inv.getSlot(SlotIndex.of(slot.getSlotIndex()));
			if (!slot1.isPresent()) {
				it.remove();
				continue;
			}
			Slot query = slot1.get();
			deInitializeItemStack(character, slot);
			if (checkForSlot(character, query)) {
				initializeItemStack(character, query);
				updateEquipOrder(character, slot);
			}
		}
		revalidateCaches(character);
	}


	@Override
	public void onRightClick(IActiveCharacter character, int slot, Slot hotbarSlot) {
		onHandInteract(character, slot, hotbarSlot);
	}

	@Override
	public void onLeftClick(IActiveCharacter character, int slot, Slot hotbarSlot) {
		onHandInteract(character, slot, hotbarSlot);
	}


	/**
	 * A method which
	 * - adds enchantments to entity effects cache
	 * - todo: apply attribute bonuses
	 *
	 * @param character player
	 * @param slot Slot having an item to be equipied
	 */
	@Override
	public boolean processSlotInteraction(IActiveCharacter character, Slot slot) {
		if (character.getPlayer().getOpenInventory().isPresent() && isOffHandSlot(slot)) {
			Optional<ItemStack> peek = slot.peek();
			if (peek.isPresent()) {
				ItemStack itemStack = peek.get();
				RPGItemType fromItemStack = itemService().getFromItemStack(itemStack);
				if (fromItemStack != null) {
					CannotUseItemReason result;
					if (fromItemStack.getWeaponClass() == WeaponClass.ARMOR || fromItemStack.getWeaponClass() == WeaponClass.SHIELD) {
						result = inventoryService().canWear(itemStack, character, fromItemStack);
					} else {
						result = inventoryService().canUse(itemStack, character, fromItemStack, HandTypes.MAIN_HAND);
					}
					if (result != CannotUseItemReason.OK) {
						return true;
					}
				}
				Boolean revalidateCache = initializeOffHandSlot(character, itemStack);
				if (revalidateCache == null) {
					return true;
				}
				if (revalidateCache) {
					revalidateCaches(character);
				}
			} else {
				deInitializeItemStack(character, HandTypes.OFF_HAND);
				revalidateCaches(character);
			}
		} else if (inventoryService().getEffectSourceBySlotId(slot) != null) {
			EquipedSlot equipedSlot = EquipedSlot.from(slot);
			CustomItem customItem = character.getEquipedInventorySlots().get(equipedSlot);
			//item has been taken away from the slot
			if (!slot.peek().isPresent()) {
				if (customItem != null) {
					character.getEquipedInventorySlots().remove(equipedSlot);
					deInitializeItemStack(character, equipedSlot);
				}
				return false;
			} else {
				ItemStack itemStack = slot.peek().get();
				RPGItemType fromItemStack = itemService().getFromItemStack(itemStack);
				if (fromItemStack == null) {
					return false;
				}
				boolean canUse;
				if (fromItemStack.getWeaponClass() == WeaponClass.ARMOR) {
					canUse = checkForArmorItem(character, itemStack, fromItemStack);
				} else {
					canUse = checkForItem(character, itemStack, fromItemStack, HandTypes.MAIN_HAND);
				}
				if (!canUse) {
					return true;
				}

				//no item before
				if (customItem == null) {
					CustomItem ci = initializeItemStack(character, slot);
					character.getEquipedInventorySlots().put(equipedSlot, ci);
				} else {
					deInitializeItemStack(character, equipedSlot);
					CustomItem ci = initializeItemStack(character, slot);
					character.getEquipedInventorySlots().put(equipedSlot, ci);
				}
				updateEquipOrder(character, equipedSlot);
			}
		}

		return false;
	}


	protected void onHandInteract(IActiveCharacter character, int slot, Slot theslot) {
		int mainHandSlotId = character.getMainHandSlotId();
		if (slot != mainHandSlotId) {
			EquipedSlot eq = EquipedSlot.from(theslot);
			Optional<ItemStack> peek = theslot.peek();
			if (!peek.isPresent()) {
				CustomItem customItem = character.getMainHand();
				if (customItem != null) {
					deInitializeItemStack(character, eq);
				}
				character.setMainHand(null, -1);
				revalidateCaches(character);
				return;
			}
			ItemStack itemStack = peek.get();
			RPGItemType fromItemStack = itemService().getFromItemStack(itemStack);
			if (fromItemStack == null) {
				CustomItem customItem = character.getMainHand();
				if (customItem != null) {
					deInitializeItemStack(character, eq);
				}
				character.setMainHand(null, -1);
				revalidateCaches(character);
				return;
			}
			CannotUseItemReason cannotUseItemReason = inventoryService().canUse(itemStack, character, fromItemStack, HandTypes.MAIN_HAND);
			if (cannotUseItemReason != CannotUseItemReason.OK) {
				CustomItem customItem = character.getMainHand();
				if (customItem != null) {
					deInitializeItemStack(character, eq);
					character.setMainHand(null, -1);
					revalidateCaches(character);
				}
				Gui.sendCannotUseItemNotification(character, itemStack, cannotUseItemReason);
			} else {
				CustomItem customItem = initializeItemStack(character, theslot);
				character.setMainHand(customItem, slot);
			}
			revalidateCaches(character);
		}
	}

	protected void updateEquipOrder(IActiveCharacter character, EquipedSlot curent) {
		List<EquipedSlot> inventoryEquipSlotOrder = character.getCharacterBase().getInventoryEquipSlotOrder();
		Iterator<EquipedSlot> iterator = inventoryEquipSlotOrder.iterator();
		while (iterator.hasNext()) {
			EquipedSlot next = iterator.next();
			if (next.equals(curent)) {
				iterator.remove();
				break;
			}
		}
		inventoryEquipSlotOrder.add(curent);
		saveQuery.add(character.getCharacterBase().getUuid());
	}

	public void updateSlotEquipOrder(CharacterBase characterBase) {
		final UUID charUUid = characterBase.getUuid();
		saveQuery.remove(charUUid);
		NtRpgPlugin.asyncExecutor.schedule(() -> {
			String hql = "update CharacterBase b set b.inventoryEquipSlotOrder = :slots where b.uuid = :uuid";
			Map<String, Object> map = new HashMap<>();
			map.put("uuid", charUUid);
			map.put("slots", characterBase.getInventoryEquipSlotOrder());
			directAccessDao.update(hql, map);
		}, 1, TimeUnit.MILLISECONDS);
	}

	@Listener(order = Order.LAST)
	public void onInventoryClose(InteractInventoryEvent.Close event, @Root Player player) {
		IActiveCharacter character = characterService().getCharacter(player);
		if (character == null) {
			//called after player dc
			return;
		}
		if (!character.isStub() && saveQuery.contains(character.getCharacterBase().getUuid())) {
			updateSlotEquipOrder(character.getCharacterBase());
		}
	}


	@Listener
	@Exclude({
			ChangeInventoryEvent.Pickup.class,
			ChangeInventoryEvent.Held.class,
			ChangeInventoryEvent.SwapHand.class,
			ChangeInventoryEvent.Transfer.class,
			ChangeInventoryEvent.Equipment.class
	})
	public void onItemGive(ChangeInventoryEvent event, @Root Player player) {
		if (event.getTargetInventory() instanceof Hotbar) {
			Cause cause = event.getCause();
		}
	}
}
