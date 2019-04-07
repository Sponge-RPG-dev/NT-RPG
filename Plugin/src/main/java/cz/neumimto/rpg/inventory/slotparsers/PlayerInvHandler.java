package cz.neumimto.rpg.inventory.slotparsers;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.CannotUseItemReason;
import cz.neumimto.rpg.inventory.SpongeInventoryService;
import cz.neumimto.rpg.inventory.SpongeItemService;
import cz.neumimto.rpg.persistance.model.EquipedSlot;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.Optional;

/**
 * Created by NeumimTo on 25.3.2018.
 */
public abstract class PlayerInvHandler implements CatalogType {

	private final String name;
	private final String id;

	protected Class<?> NMS_PLAYER_INVENTORY;
	protected int NMS_PLAYER_INVENTORY_OFFHAND_SLOT_ID;

	public PlayerInvHandler(String name) {
		this.id = "nt-rpg:" + name.toLowerCase();
		this.name = name;
		try {
			//should exist
			NMS_PLAYER_INVENTORY = Class.forName("net.minecraft.entity.player.InventoryPlayer");
		} catch (ClassNotFoundException e) {
			NMS_PLAYER_INVENTORY = null;
			e.printStackTrace();
		}
		NMS_PLAYER_INVENTORY_OFFHAND_SLOT_ID = 40;
	}

	/**
	 * Called after loading main plugin config
	 */
	public abstract void initHandler();

	/**
	 * Method called when
	 *  - Player connects
	 *  - Player changes world
	 *  - Player gain level
	 *  - Player learn/upgrade skill
	 *  - Player changes weapon x
	 *  - Player changes armor/accessories x
	 *
	 *  Item init order is in a way player equiped items
	 *  @see cz.neumimto.rpg.players.CharacterBase#inventoryEquipSlotOrder
	 *  As last
	 *
	 * @param character The character
	 */
	public abstract void initializeCharacterInventory(IActiveCharacter character);


	protected boolean checkForSlot(IActiveCharacter character, Slot slot) {
		Optional<ItemStack> peek = slot.peek();
		if (peek.isPresent()) {
			ItemStack itemStack = peek.get();
			RpgItemStack fromItemStack = itemService().getFromItemStack(itemStack);
			if (fromItemStack == null) {
				return true;
			}
			if (fromItemStack.getWeaponClass() == WeaponClass.ARMOR) {
				return checkForItem(character, itemStack, fromItemStack, HandTypes.MAIN_HAND);
			} else {
				return checkForArmorItem(character, itemStack, fromItemStack);
			}
		}
		return true;
	}

	protected boolean checkForItem(IActiveCharacter character, ItemStack itemStack, RpgItemStack itemType, HandType h) {
		CannotUseItemReason cannotUseItemReason = inventoryService().canUse(itemStack, character, itemType, h);
		return cannotUseItemReason == CannotUseItemReason.OK;
	}

	protected boolean checkForArmorItem(IActiveCharacter character, ItemStack itemStack, RpgItemStack itemType) {
		CannotUseItemReason cannotUseItemReason = inventoryService().canWear(itemStack, character, itemType);
		return cannotUseItemReason == CannotUseItemReason.OK;
	}


	/**
	 * A method which
	 * - adds enchantments to entity effects cache
	 * - todo: apply attribute bonuses
	 *
	 * @param character player
	 * @param query Slot having an item to be equipied
	 */
	protected RpgItemStack initializeItemStack(IActiveCharacter character, Slot query) {
		Optional<ItemStack> oItemStack = query.peek();
		if (oItemStack.isPresent()) {
			ItemStack itemStack = oItemStack.get();
			CustomItemToRemove customItem = CustomItemFactory.createCustomItem(itemStack, query);
			effectService().applyGlobalEffectsAsEnchantments(customItem.getEffects(), character, customItem); //todo
			return customItem;
		}
		return null;
	}

	protected void deInitializeItemStack(IActiveCharacter character, EquipedSlot query) {
		CustomItemToRemove item = character.getEquipedInventorySlots().get(query);
		if (item != null) {
			effectService().removeGlobalEffectsAsEnchantments(item.getEffects().keySet(), character, item);
		}
		character.getEquipedInventorySlots().remove(query);
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * @param character character instance
	 * @param slot clicked slot, the slot is NOT in hotbar row, the slot might be offhand
	 * @return true if the click inventory event shall be cancelled
	 */
	public abstract boolean processSlotInteraction(IActiveCharacter character, Slot slot);

	public abstract void onRightClick(IActiveCharacter character, int slot, Slot hotbarSlot);

	public abstract void onLeftClick(IActiveCharacter character, int slot, Slot hotbarSlot);


	protected SpongeInventoryService inventoryService() {
		return NtRpgPlugin.GlobalScope.inventorySerivce;
	}

	protected SpongeItemService itemService() {
		return NtRpgPlugin.GlobalScope.itemService;
	}

	protected EffectService effectService() {
		return NtRpgPlugin.GlobalScope.effectService;
	}

	protected CharacterService characterService() {
		return NtRpgPlugin.GlobalScope.characterService;
	}

	protected DamageService damageService() {
		return NtRpgPlugin.GlobalScope.damageService;
	}


	public void processHotbarItemDispense(IActiveCharacter character) {
		CustomItemToRemove mainHand = character.getMainHand();
		if (mainHand != null) {
			Hotbar query = character.getPlayer().getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class));
			Optional<Slot> slot = query.getSlot(new SlotIndex(query.getSelectedSlotIndex()));
			EquipedSlot equipedSlot = EquipedSlot.from(slot.get());
			deInitializeItemStack(character, equipedSlot);
			character.setMainHand(null, -1);
			revalidateCaches(character);
		}
	}

	protected void revalidateCaches(IActiveCharacter character) {
		damageService().recalculateCharacterWeaponDamage(character);
	}

	/**
	 *
	 * @param character
	 * @param futureMainHand
	 * @param futureOffHand
	 * @return True if the event shall be cancelled
	 */
	public boolean processHotbarSwapHand(IActiveCharacter character, ItemStack futureMainHand, ItemStack futureOffHand) {
		/* Slot dispense */
		boolean recalc = false;
		if (futureMainHand == null) {
			CustomItemToRemove mainHand = character.getMainHand();
			if (mainHand != null) {
				deInitializeItemStack(character, HandTypes.MAIN_HAND);
				recalc = true;
			}
		} else {
			RPGItemTypeToRemove fromItemStack = itemService().getFromItemStack(futureMainHand);
			if (fromItemStack != null) {
				CannotUseItemReason reason;
				if (fromItemStack.getWeaponClass() == WeaponClass.SHIELD || fromItemStack.getWeaponClass() == WeaponClass.ARMOR) {
					reason = inventoryService().canWear(futureOffHand, character, fromItemStack);
				} else {
					reason = inventoryService().canUse(futureOffHand, character, fromItemStack, HandTypes.MAIN_HAND);
				}
				if (reason == CannotUseItemReason.OK) {
					deInitializeItemStack(character, HandTypes.MAIN_HAND);
					initializeItemStack(character, HandTypes.MAIN_HAND,
							CustomItemFactory.createCustomItemForHandSlot(futureMainHand, HandTypes.MAIN_HAND));
					recalc = true;
				} else {
					Gui.sendCannotUseItemNotification(character, futureMainHand, reason);
					return true;
				}
			} else {
				deInitializeItemStack(character, HandTypes.OFF_HAND);
			}
		}
		if (futureOffHand == null) {
			CustomItemToRemove offHand = character.getOffHand();
			if (offHand != null) {
				deInitializeItemStack(character, HandTypes.OFF_HAND);
				recalc = true;
			}
		} else {
			RPGItemTypeToRemove fromItemStack = itemService().getFromItemStack(futureOffHand);
			if (fromItemStack != null) {
				CannotUseItemReason reason;
				if (fromItemStack.getWeaponClass() == WeaponClass.SHIELD || fromItemStack.getWeaponClass() == WeaponClass.ARMOR) {
					reason = inventoryService().canWear(futureOffHand, character, fromItemStack);
				} else {
					reason = inventoryService().canUse(futureOffHand, character, fromItemStack, HandTypes.OFF_HAND);
				}
				if (reason == CannotUseItemReason.OK) {
					deInitializeItemStack(character, HandTypes.OFF_HAND);
					initializeItemStack(character, HandTypes.OFF_HAND,
							CustomItemFactory.createCustomItemForHandSlot(futureMainHand, HandTypes.OFF_HAND));
					recalc = true;
				} else {
					Gui.sendCannotUseItemInOffHandNotification(character, futureOffHand, reason);
					return true;
				}
			} else {
				deInitializeItemStack(character, HandTypes.MAIN_HAND);
			}
		}
		if (Boolean.TRUE.equals(initializeOffHandSlot(character, futureOffHand))) {
			recalc = true;
		}
		if (recalc) {
			revalidateCaches(character);
		}
		return false;
	}

	/**
	 *
	 * @param character
	 * @param futureOffHand
	 * @return True if damage cache should be re-validated, null if item cannto be used in the offhand
	 */
	public Boolean initializeOffHandSlot(IActiveCharacter character, ItemStack futureOffHand) {
		RpgItemStack oh = character.getOffHand();
		if (oh != null) {
			deInitializeItemStack(character, HandTypes.OFF_HAND);
			return true;
		}
		if (futureOffHand != null) {
			RPGItemTypeToRemove fromItemStack = itemService().getFromItemStack(futureOffHand);
			if (fromItemStack != null && fromItemStack.getWeaponClass() == WeaponClass.SHIELD) {
				if (character.canWear(fromItemStack)) {
					CustomItemToRemove customItem = CustomItemFactory.createCustomItemForHandSlot(futureOffHand, HandTypes.OFF_HAND);
					initializeItemStack(character, HandTypes.OFF_HAND, customItem);
				}
			} else if (inventoryService().canUse(futureOffHand, character, fromItemStack, HandTypes.OFF_HAND) != CannotUseItemReason.OK) {
				return null;
			}
		}
		return false;
	}

	public void initializeItemStack(IActiveCharacter character, HandType handType, RpgItemStack customItem) {
		if (handType == HandTypes.OFF_HAND) {
			character.setOffHand(customItem);
		} else {
			int slotIndex =
					((Hotbar) character.getPlayer().getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class)))
							.getSelectedSlotIndex();
			character.setMainHand(customItem, slotIndex);
		}
		effectService().applyGlobalEffectsAsEnchantments(customItem.getEffects(), character, customItem); //todo
	}

	public void deInitializeItemStack(IActiveCharacter character, HandType handType) {
		RpgItemStack customItem;
		if (handType == HandTypes.OFF_HAND) {
			customItem = character.getOffHand();
			character.setOffHand(null);
			if (customItem != null) {
				effectService().removeGlobalEffectsAsEnchantments(customItem.getEffects().keySet(), character, customItem);
			}
		} else {
			customItem = character.getMainHand();
			character.setMainHand(null, -1);
			if (customItem != null) {
				effectService().removeGlobalEffectsAsEnchantments(customItem.getEffects().keySet(), character, customItem);
			}
		}

	}


	public boolean isOffHandSlot(Slot slot) {
		Inventory transform = slot.transform();
		Inventory parent = transform.parent();
		return parent.getClass() == NMS_PLAYER_INVENTORY
				&& transform.getInventoryProperty(SlotIndex.class).get().getValue() == NMS_PLAYER_INVENTORY_OFFHAND_SLOT_ID;
	}
}
