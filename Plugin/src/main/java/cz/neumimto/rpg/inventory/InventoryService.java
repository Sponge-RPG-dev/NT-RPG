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

package cz.neumimto.rpg.inventory;

import com.google.common.collect.ImmutableList;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.data.CustomItemData;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.utils.ItemStackUtils;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class InventoryService {


	public static ItemType ITEM_SKILL_BIND = ItemTypes.BLAZE_POWDER;

	public static TextColor LORE_FIRSTLINE = TextColors.AQUA;
	public static TextColor SOCKET_COLOR = TextColors.GRAY;
	public static TextColor ENCHANTMENT_COLOR = TextColors.BLUE;
	public static TextColor LEVEL_COLOR = TextColors.DARK_GRAY;
	public static TextColor RESTRICTIONS = TextColors.LIGHT_PURPLE;
	public static TextColor DELIMITER = TextColors.GRAY;
	public static TextColor LORE_COLOR = TextColors.GOLD;
	public static TextColor RUNEWORD_NAME = TextColors.DARK_RED;
	public static TextColor RUNEWORD_LORE = TextColors.RED;
	public static TextStyle LORE_STYLE = TextStyles.ITALIC;

	public static Pattern REGEXP_NUMBER = Pattern.compile("-?\\d+");

	public static Text NORMAL_RARITY;

	@Inject
	private SkillService skillService;

	@Inject
	private Game game;

	@Inject
	private CharacterService characterService;

	@Inject
	private EffectService effectService;

	@Inject
	private DamageService damageService;

	@Inject
	private RWService rwService;

	private Map<UUID, InventoryMenu> inventoryMenus = new HashMap<>();


	@PostProcess
	public void init() {
		NORMAL_RARITY = Text.of(Localization.NORMAL_RARITY);
	}

	public ItemStack getHelpItem(List<String> lore, ItemType type) {
		ItemStack.Builder builder = ItemStack.builder();
		builder.quantity(1).itemType(type);
		return builder.build();
	}

	public Map<UUID, InventoryMenu> getInventoryMenus() {
		return inventoryMenus;
	}

	public void addInventoryMenu(UUID uuid, InventoryMenu menu) {
		if (!inventoryMenus.containsKey(uuid))
			inventoryMenus.put(uuid, menu);
	}

	public void initializeHotbar(IActiveCharacter character) {
		if (character.isStub())
			return;
		Hotbar hotbar = character.getPlayer().getInventory().query(Hotbar.class);
		int slot = 0;
		for (Inventory inventory : hotbar) {
			initializeHotbar(character, slot, null, (Slot) inventory, hotbar);
			slot++;
		}

	}

	public void initializeHotbar(IActiveCharacter character, int slot) {
		initializeHotbar(character, slot, null);
	}

	public void initializeHotbar(IActiveCharacter character, int slot, ItemStack toPickup) {
		Player player = character.getPlayer();
		Hotbar hotbar = player.getInventory().query(Hotbar.class);

		Optional<Slot> slot1 = hotbar.getSlot(new SlotIndex(slot));
		if (slot1.isPresent()) {
			initializeHotbar(character, slot, toPickup, slot1.get(), hotbar);
		}
	}

	public void initializeHotbar(IActiveCharacter character, int slot, ItemStack toPickup, Slot s, Hotbar hotbar) {
		if (hotbar == null) {
			hotbar = character.getPlayer().getInventory().query(Hotbar.class);
		}
		int selectedSlotIndex = hotbar.getSelectedSlotIndex();
		Optional<ItemStack> peek = s.peek();
		if (!peek.isPresent()) {
			//picking up an item
			if (character.getHotbar()[slot] != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
				HotbarObject hotbarObject = character.getHotbar()[slot];
				hotbarObject.onUnEquip(character);
				character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
			}
			if (toPickup != null) {
				HotbarObject o = getHotbarObject(character, toPickup);
				if (o != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
					o.setSlot(slot);
					if (o.getType() == EffectSourceType.WEAPON && slot == selectedSlotIndex) {
						o.onRightClick(character); //simulate player interaction to equip the weapon
					} else if (o.getType() == EffectSourceType.CHARM) {
						o.onEquip(character);
					}
					character.getHotbar()[slot] = o;
				}
			}
		} else {
			if (character.getHotbar()[slot] != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
				character.getHotbar()[slot].onUnEquip(character);
			}
			ItemStack i = peek.get();
			HotbarObject hotbarObject = getHotbarObject(character, i);
			if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
				hotbarObject.setSlot(slot);
				character.getHotbar()[slot] = hotbarObject;
				CannotUseItemReson reason = canWear(i, character);
				if (reason != CannotUseItemReson.OK) {
					ItemStack itemStack = s.poll().get();
					dropItem(character, itemStack, reason);
					character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
					return;
				}
				if (hotbarObject.getHotbarObjectType() == HotbarObjectTypes.CHARM) {
					hotbarObject.onEquip(character);
				} else if (hotbarObject.getHotbarObjectType() == HotbarObjectTypes.WEAPON && slot == selectedSlotIndex) {
					hotbarObject.onRightClick(character); //simulate player interaction to equip the weapon
					((Weapon) hotbarObject).setCurrent(true);
				}

			} else {
				character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
			}

		}
	}

	protected Armor getHelmet(IActiveCharacter character) {
		if (character.isStub()) {
			return Armor.NONE;
		}
		Optional<ItemStack> leggings = character.getPlayer().getHelmet();
		if (leggings.isPresent()) {
			ItemStack itemStack = leggings.get();
			return getArmor(itemStack, EffectSourceType.HELMET);
		}
		return Armor.NONE;
	}

	protected Armor getChestplate(IActiveCharacter character) {
		if (character.isStub()) {
			return Armor.NONE;
		}
		Optional<ItemStack> leggings = character.getPlayer().getChestplate();
		if (leggings.isPresent()) {
			ItemStack itemStack = leggings.get();
			return getArmor(itemStack, EffectSourceType.CHESTPLATE);
		}
		return Armor.NONE;
	}

	protected Armor getLeggings(IActiveCharacter character) {
		if (character.isStub()) {
			return Armor.NONE;
		}
		Optional<ItemStack> leggings = character.getPlayer().getLeggings();
		if (leggings.isPresent()) {
			ItemStack itemStack = leggings.get();
			return getArmor(itemStack, EffectSourceType.LEGGINGS);
		}
		return Armor.NONE;
	}

	protected Armor getBoots(IActiveCharacter character) {
		if (character.isStub()) {
			return Armor.NONE;
		}
		Optional<ItemStack> leggings = character.getPlayer().getBoots();
		if (leggings.isPresent()) {
			ItemStack itemStack = leggings.get();
			return getArmor(itemStack, EffectSourceType.BOOTS);
		}
		return Armor.NONE;
	}

	private Armor getArmor(ItemStack itemStack, IEffectSource armorType) {
		CustomItemData itemData = getItemData(itemStack);
		Armor armor = new Armor(itemStack, armorType);
		armor.setEffects(getItemEffects(itemStack));
		armor.setLevel(itemData.itemLevel().get());
		return armor;
	}

	public void initializeArmor(IActiveCharacter character) {
		Optional<ItemStack> chestplate = character.getPlayer().getChestplate();
		ItemStack is = null;
		if (chestplate.isPresent()) {
			is = chestplate.get();
			CannotUseItemReson reason = canWear(is, character);
			if (reason != CannotUseItemReson.OK) {
				character.getPlayer().setChestplate(null);
				dropItem(character, is, reason);
			} else {

				Armor armor = getChestplate(character);

				Armor armor1 = character.getEquipedArmor().get(EquipmentTypes.CHESTPLATE);
				if (armor1 != null) {
					effectService.removeGlobalEffectsAsEnchantments(armor1.getEffects(), character, armor1);
				}
				character.getEquipedArmor().put(EquipmentTypes.CHESTPLATE, armor);
				effectService.applyGlobalEffectsAsEnchantments(armor.getEffects(), character, armor);

			}
		}

		Optional<ItemStack> helmet = character.getPlayer().getHelmet();
		if (helmet.isPresent()) {
			is = helmet.get();
			CannotUseItemReson reason = canWear(is, character);
			if (reason != CannotUseItemReson.OK) {
				character.getPlayer().setHelmet(null);
				dropItem(character, is, reason);
			} else {

				Armor armor = getHelmet(character);

				Armor armor1 = character.getEquipedArmor().get(EquipmentTypes.HEADWEAR);
				if (armor1 != null) {
					effectService.removeGlobalEffectsAsEnchantments(armor1.getEffects(), character, armor1);
				}
				character.getEquipedArmor().put(EquipmentTypes.HEADWEAR, armor);
				effectService.applyGlobalEffectsAsEnchantments(armor.getEffects(), character, armor);

			}
		}
		Optional<ItemStack> boots = character.getPlayer().getBoots();
		if (boots.isPresent()) {
			is = boots.get();
			CannotUseItemReson reason = canWear(is, character);
			if (reason != CannotUseItemReson.OK) {
				character.getPlayer().setBoots(null);
				dropItem(character, is, reason);
			} else {
				Armor armor = getBoots(character);
				Armor armor1 = character.getEquipedArmor().get(EquipmentTypes.BOOTS);
				if (armor1 != null) {
					effectService.removeGlobalEffectsAsEnchantments(armor1.getEffects(), character, armor1);
				}
				character.getEquipedArmor().put(EquipmentTypes.BOOTS, armor);
				effectService.applyGlobalEffectsAsEnchantments(armor.getEffects(), character, armor);
			}
		}
		Optional<ItemStack> leggings = character.getPlayer().getLeggings();
		if (leggings.isPresent()) {
			is = leggings.get();
			CannotUseItemReson reason = canWear(is, character);
			if (reason != CannotUseItemReson.OK) {
				character.getPlayer().setLeggings(null);

				dropItem(character, is, reason);
			} else {
				Armor armor = getLeggings(character);

				Armor armor1 = character.getEquipedArmor().get(EquipmentTypes.LEGGINGS);
				if (armor1 != null) {
					effectService.removeGlobalEffectsAsEnchantments(armor1.getEffects(), character, armor1);
				}
				character.getEquipedArmor().put(EquipmentTypes.LEGGINGS, armor);
				effectService.applyGlobalEffectsAsEnchantments(armor.getEffects(), character, armor);
			}
		}
	}


	private void dropItem(IActiveCharacter character, ItemStack is, CannotUseItemReson reason) {
		ItemStackUtils.dropItem(character.getPlayer(), is);
		Gui.sendCannotUseItemNotification(character, is, reason);
	}

	protected HotbarObject getHotbarObject(IActiveCharacter character, ItemStack is) {
		if (is == null)
			return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
		if (ItemStackUtils.isItemSkillBind(is)) {
			return buildHotbarSkill(character, is);
		}
		if (ItemStackUtils.isCharm(is)) {
			return buildCharm(character, is);
		}
		if (ItemStackUtils.isItemRune(is)) {
			return new HotbarRune(is);
		}
		if (ItemStackUtils.isWeapon(is.getItem())) {
			return buildHotbarWeapon(character, is);
		}
		return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
	}

	private Charm buildCharm(IActiveCharacter character, ItemStack is) {
		Charm charm = new Charm(is);
		charm.setEffects(getItemEffects(is));

		return charm;
	}

	private HotbarRune buildHotbarRune(ItemStack is) {
		HotbarRune rune = new HotbarRune(is);
		Optional<Text> text = is.get(Keys.DISPLAY_NAME);
		if (text.isPresent()) {
			String s = text.get().toPlain();
			Rune rune1 = rwService.getRune(s);
			rune.r = rune1;
		}
		return rune;
	}

	public Weapon buildHotbarWeapon(IActiveCharacter character, ItemStack is) {
		Weapon w = new Weapon(is);
		Optional<List<Text>> a = is.get(Keys.ITEM_LORE);
		if (!a.isPresent()) {
			return w;
		}
		w.setItemData(getItemData(is));
		List<Text> texts = a.get();
		CustomItemData itemData = w.getCustomItemData();
		w.setLevel(itemData.itemLevel().get());
		w.setEffects(getItemEffects(is));
		return w;
	}

	public HotbarSkill buildHotbarSkill(IActiveCharacter character, ItemStack is) {
		HotbarSkill skill = new HotbarSkill(is);
		Optional<Text> text = is.get(Keys.DISPLAY_NAME);
		if (text.isPresent()) {
			String s = text.get().toPlain();
			String[] split = s.split("-");
			for (String s1 : split) {
				if (s1.isEmpty())
					continue;
				if (s1.endsWith("«")) {
					String substring = s1.substring(0, s1.length() - 2);
					skill.left_skill = skillService.getSkill(substring);
				} else if (s1.startsWith("»")) {
					String substring = s1.substring(2);
					skill.right_skill = skillService.getSkill(substring);
				}
			}
		}
		return skill;
	}

	public void createHotbarSkill(ItemStack is, ISkill right, ISkill left) {
		Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
		List<Text> lore;
		if (texts.isPresent()) {
			lore = texts.get();
			lore.clear();
		} else {
			lore = new ArrayList<>();
		}
		lore.add(Text.of(LORE_FIRSTLINE, Localization.SKILLBIND));
		is.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.ITALIC, left != null ? left.getName() + " «-" : "", right != null ? "-» " + right.getName() : ""));
		if (right != null) {
			lore.add(Text.of(TextColors.RED, Localization.CAST_SKILL_ON_RIGHTLICK.replaceAll("%1", right.getName())));
			lore = makeDesc(right, lore);
		}
		if (left != null) {
			lore.add(Text.of(TextColors.RED, Localization.CAST_SKILl_ON_LEFTCLICK.replaceAll("%1", left.getName())));
			lore = makeDesc(left, lore);
		}
		for (String a : Localization.ITEM_SKILLBIND_FOOTER.split(":n")) {
			lore.add(Text.of(TextColors.DARK_GRAY, a));
		}
		ItemStackUtils.createEnchantmentGlow(is);
		is.offer(Keys.ITEM_LORE, lore);
	}

	private List<Text> makeDesc(ISkill skill, List<Text> lore) {
		if (skill.getDescription() != null) {
			for (String s : skill.getDescription().split(":n")) {
				lore.add(Text.of(TextColors.GRAY, "- " + s));
			}
		}
		if (skill.getLore() != null) {
			for (String s : skill.getLore().split(":n")) {
				lore.add(Text.of(TextColors.GREEN, TextStyles.ITALIC, s));
			}
		}
		return lore;
	}

	//todo event
	public void onRightClick(IActiveCharacter character, int slot) {
		HotbarObject hotbarObject = character.getHotbar()[slot];
		if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
			hotbarObject.onRightClick(character);
		}
	}

	public void onLeftClick(IActiveCharacter character, int slot) {
		HotbarObject hotbarObject = character.getHotbar()[slot];
		if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
			hotbarObject.onLeftClick(character);
		}
	}

	protected void changeEquipedWeapon(IActiveCharacter character, Weapon changeTo) {
		unEquipWeapon(character);

		int slot = ((Hotbar) character.getPlayer().getInventory().query(Hotbar.class)).getSelectedSlotIndex();
		character.setHotbarSlot(slot, changeTo);
		changeTo.current = true;
		changeTo.setSlot(slot);
		character.setMainHand(changeTo);
		changeTo.onEquip(character);
		damageService.recalculateCharacterWeaponDamage(character, changeTo.getItemType());
	}

	private void unEquipWeapon(IActiveCharacter character) {
		Weapon mainHand = character.getMainHand();
		mainHand.current = false;
		mainHand.onUnEquip(character);
	}

	public void startSocketing(IActiveCharacter character) {
		Optional<ItemStack> itemInHand = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND);
		if (itemInHand.isPresent()) {
			Hotbar h = character.getPlayer().getInventory().query(Hotbar.class);
			int selectedSlotIndex = h.getSelectedSlotIndex();
			HotbarObject o = character.getHotbar()[selectedSlotIndex];
			if (o.getHotbarObjectType() == HotbarObjectTypes.RUNE) {
				character.setCurrentRune(selectedSlotIndex);
				Gui.sendMessage(character, Localization.SOCKET_HELP);
			}
		}
	}

	public void insertRune(IActiveCharacter character) {
		if (!character.isSocketing())
			return;
		Optional<ItemStack> itemInHand = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND);
		if (itemInHand.isPresent()) {
			ItemStack itemStack = itemInHand.get();
			if (ItemStackUtils.hasSockets(itemStack)) {
				HotbarObject hotbarObject = character.getHotbar()[character.getCurrentRune()];
				if (hotbarObject == HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
					character.setCurrentRune(-1);
					return;
				}
				if (hotbarObject.type == HotbarObjectTypes.RUNE) {
					HotbarRune r = (HotbarRune) hotbarObject;
					String name = null;
					Inventory slot = character.getPlayer().getInventory().query(Hotbar.class).query(new SlotIndex(character.getCurrentRune()));
					ItemStack runeitem = null;
					if (!slot.peek().isPresent()) {
						return;
					}
					runeitem = slot.peek().get();
					if (runeitem.get(Keys.DISPLAY_NAME).isPresent()) {
						name = runeitem.get(Keys.DISPLAY_NAME).get().toPlain();
					}
					r.r = rwService.getRune(name);
					if (r.r == null) {
						Gui.sendMessage(character, Localization.UNKNOWN_RUNE_NAME);
						character.setCurrentRune(-1);
						return;
					}
					ItemStack i = rwService.insertRune(itemStack, r.getRune().getName());
					CarriedInventory<? extends Carrier> inventory = character.getPlayer().getInventory();
					Inventory query = inventory.query(Hotbar.class).query(new SlotIndex(character.getCurrentRune()));
					query.clear();
					character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, i);
					List<Text> texts = i.get(Keys.ITEM_LORE).get();
					if (!rwService.hasEmptySocket(texts)) {
						RuneWord rw = rwService.runeWordByCombinationAfterInsert(texts);
						i = rwService.reBuildRuneword(i, rw);
						if (rwService.canUse(rw, character)) {
							character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, i);
						} else {
							character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, null);
							Entity entity = character.getPlayer().getLocation().getExtent().createEntity(EntityTypes.ITEM, character.getPlayer().getLocation().getPosition());
							entity.offer(Keys.REPRESENTED_ITEM, i.createSnapshot());
							character.getPlayer().getWorld().spawnEntity(entity);
						}
					}
				}
			}
		}
	}

	public void reinitializePlayerInventory(IActiveCharacter character) {
		Inventory i = character.getPlayer().getInventory();
		EquipmentInventory inventory = character.getPlayer().getInventory().query(EquipmentInventory.class);
		for (Integer integer : character.getSlotsToReinitialize()) {
			//todo
		}
		character.getSlotsToReinitialize().clear();
	}


	public CannotUseItemReson canWear(ItemStack itemStack, IActiveCharacter character) {
		if (ItemStackUtils.any_armor.contains(itemStack.getItem())) {
			if (!character.canWear(itemStack)) {
				return CannotUseItemReson.CONFIG;
			}
		}
		CustomItemData itemData = getItemData(itemStack);
		return checkRestrictions(character, itemData);

	}

	public CannotUseItemReson canUse(ItemStack itemStack, IActiveCharacter character) {
		if (ItemStackUtils.weapons.contains(itemStack.getItem())) {
			if (!character.canUse(itemStack.getItem())) {
				return CannotUseItemReson.CONFIG;
			}
		}
		return checkRestrictions(character, getItemData(itemStack));
	}

	private CannotUseItemReson checkRestrictions(IActiveCharacter character, CustomItemData itemData) {
		ListValue<String> strings = itemData.groupRestricitons();
		if (strings.isEmpty())
			return CannotUseItemReson.OK;
		int k = 0;

		for (String string : strings) {
			if (string.contains(character.getRace().getName())) {
				k++;
				continue;
			}
			for (ExtendedNClass extendedNClass : character.getClasses()) {
				k++;
				if (string.contains(extendedNClass.getConfigClass().getName())) {
					continue;
				}
			}
		}

		if (strings.size() == k) {
			if (character.getPrimaryClass().getLevel() < itemData.itemLevel().get()) {
				return CannotUseItemReson.OK;
			} else {
				return CannotUseItemReson.LEVEL;
			}
		} else {
			return CannotUseItemReson.LORE;
		}
	}


	public void cancelSocketing(IActiveCharacter character) {
		if (character.isSocketing()) {
			Gui.sendMessage(character, Localization.SOCKET_CANCELLED);
		}
		character.setCurrentRune(-1);

	}

	public Set<String> getItemRarityTypes() {
		return new HashSet<>(Arrays.asList(Localization.RUNEWORD,
				Localization.SOCKET,
				Localization.RUNE,
				Localization.CHARM,
				Localization.SKILLBIND));

	}


	//todo
	private void initializeSlots(IActiveCharacter character) {
		for (Integer integer : character.getSlotsToReinitialize()) {
			if (Utils.isHotbar(integer)) {
				initializeHotbar(character, integer);
			} else {
				// initializeArmor(character, integer);
			}
		}
	}


	/**
	 * Rarity
	 * ItemLevel/sockets
	 * <p>
	 * Enchantments
	 * <p>
	 * Restrictions
	 * <p>
	 * Lore
	 *
	 * @param itemStack
	 * @param restrictions
	 */
	public void setRestrictions(ItemStack itemStack, List<String> restrictions, int level) {
		CustomItemData itemData = getItemData(itemStack);
		if (restrictions.isEmpty()) {
			//todo
		}
	}

	public ItemStack setEnchantments(Map<String, String> effects, ItemStack itemStack) {
		CustomItemData itemData = getItemData(itemStack);
		Map<String, String> map = new HashMap<>();
		map.putAll(itemData.getEnchantements());
		map.putAll(effects);
		itemData.setEnchantements(map);
		itemStack.offer(itemData);
		return updateLore(itemStack);
	}

	public ItemStack setItemRarity(ItemStack itemStack, Text rarity) {
		if (!getItemRarityTypes().contains(rarity.toPlain())) {
			return itemStack;
		}
		CustomItemData itemData = getItemData(itemStack);
		itemData.setRarity(rarity);
		itemStack.offer(itemData);
		return updateLore(itemStack);
	}

	public ItemStack setItemLevel(ItemStack itemStack, int level) {
		CustomItemData item = getItemData(itemStack);
		item.setItemLevel(level);
		itemStack.offer(item);
		return updateLore(itemStack);
	}

	public ItemStack updateLore(ItemStack is) {
		Optional<CustomItemData> customItemData = is.get(CustomItemData.class);
		CustomItemData data = customItemData.orElse(new CustomItemData());
		Value<Text> rarity = data.rarity();
		Text text = rarity.get();
		List<Text> lore = new ArrayList<>();
		if (!text.toPlain().isEmpty()) {
			lore.add(text);
		}
		int k = data.getSocketCount();
		if (k > 0) {
			String s = "";
			while (k > 0) {
				s += "{@}";
				k--;
			}
			lore.add(Text.builder(s).color(SOCKET_COLOR).build());
		}
		if (data.itemLevel().get() > 0) {
			lore.add(Text.builder(Localization.ITEM_LEVEL + ": " + data.itemLevel().get()).color(LEVEL_COLOR).build());
		}

		Map<String, String> map = data.enchantements().get();
		if (!map.isEmpty()) {
			lore.add(Text.EMPTY);
			for (Map.Entry<String, String> entry : map.entrySet()) {
				Text t = null;
				if (entry.getKey() == null) {
					t = Text.builder(entry.getValue()).color(ENCHANTMENT_COLOR).build();
				} else {
					t = Text.builder(entry.getKey()).color(ENCHANTMENT_COLOR)
							.append(Text.builder(": ").color(DELIMITER).style(TextStyles.BOLD).build())
							.append(Text.builder(entry.getValue()).color(ENCHANTMENT_COLOR).build())
							.build();
				}
				lore.add(t);
			}
		}
		List<String> u = data.groupRestricitons().get();
		if (!u.isEmpty()) {
			lore.add(Text.EMPTY);

			for (String a : u) {
				Text t = Text.builder(a).color(RESTRICTIONS).build();
				lore.add(t);
			}
		}
		is.offer(Keys.ITEM_LORE, lore);
		return is;
	}

	public CustomItemData getItemData(ItemStack itemStack) {
		Optional<CustomItemData> opt = itemStack.get(CustomItemData.class);
		if (opt.isPresent()) {
			return opt.get();
		}
		CustomItemData data = new CustomItemData();
		Optional<List<Text>> texts = itemStack.get(Keys.ITEM_LORE);

		if (texts.isPresent()) {
			for (Text text : texts.get()) {
				if (text.getColor() == ENCHANTMENT_COLOR) {
					String s = text.toPlainSingle();
					IGlobalEffect globalEffect = effectService.getGlobalEffect(s);
					if (globalEffect != null) {
						String a = null;
						ImmutableList<Text> children = text.getChildren();
						if (children.size() > 0) {
							Text text1 = children.get(children.size() - 1);
							a = text.toPlainSingle();
						}
						data.getEnchantements().put(globalEffect.getName(), a);
					}
				} else if (text.getColor() == LEVEL_COLOR) {
					String s = text.toPlain();
					String s1 = Utils.extractNumber(s);
					if (s1 != null) {
						data.setItemLevel(Integer.parseInt(s1));
					}
				}

			}
			itemStack.offer(data);
		}

		return data;
	}

	public Map<IGlobalEffect, String> getItemEffects(ItemStack is) {
		CustomItemData itemData = getItemData(is);
		return getItemEffects(itemData);
	}

	public Map<IGlobalEffect, String> getItemEffects(CustomItemData itemData) {
		return itemData.enchantements().get().entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> effectService.getGlobalEffect(e.getKey()),
						Map.Entry::getValue
				));
	}


	public void setSocketCount(ItemStack itemStack, int i) {
		CustomItemData itemData = getItemData(itemStack);
		itemData.setSocketCount(i);
		itemStack.offer(itemData);
	}
}
