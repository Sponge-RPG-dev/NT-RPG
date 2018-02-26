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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.Arg;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.gui.ItemLoreBuilderService;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.manipulators.EffectsData;
import cz.neumimto.rpg.inventory.data.manipulators.ItemLevelData;
import cz.neumimto.rpg.inventory.data.manipulators.ItemRarityData;
import cz.neumimto.rpg.inventory.runewords.ItemUpgrade;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class InventoryService {


	public static ItemType ITEM_SKILL_BIND = ItemTypes.BLAZE_POWDER;

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

	@Inject
	private PropertyService propertyService;

	@Inject
	private GroupService groupService;

	private Set<String> reservedItemNames = new HashSet<>();

	private Map<UUID, InventoryMenu> inventoryMenus = new HashMap<>();
	private Map<String, ItemGroup> itemGroups = new HashMap<>();

	@PostProcess(priority = 3000)
	public void init() {
		NORMAL_RARITY = Text.of(Localization.NORMAL_RARITY);
		loadItemGroups();

	}

	private void loadItemGroups() {
		Path path = Paths.get(NtRpgPlugin.workingDir+"/ItemGroups.conf");
		File f = path.toFile();
		if (!f.exists()) {
			try {
				PrintWriter writer = new PrintWriter(f);
				writer.println("ReservedItemNames:[]");
				writer.println("ItemGroups:[");
				addDefaultItemsToGroup(writer, WeaponKeys.SWORDS, "swords_damage_mult");
				addDefaultItemsToGroup(writer, WeaponKeys.AXES, "axes_damage_mult");
				addDefaultItemsToGroup(writer, WeaponKeys.SPADES, "spades_damage_mult");
				addDefaultItemsToGroup(writer, WeaponKeys.PICKAXES, "pickaxes_damage_mult");
				addDefaultItemsToGroup(writer, WeaponKeys.HOES, "hoes_damage_mult");
				addDefaultItemsToGroup(writer, WeaponKeys.BOWS, "bows_meele_damage_mult");
				addDefaultItemsToGroup(writer, WeaponKeys.STAFF, "staffs_damage_mult");
				writer.println("]");
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		Config c = ConfigFactory.parseFile(path.toFile());
		reservedItemNames.addAll(c.getStringList("ReservedItemNames"));
		for (Config itemGroups : c.getConfigList("ItemGroups")) {
			List<String> items = itemGroups.getStringList("Items");
			String groupName = itemGroups.getString("ItemGroupName");
			ItemGroup itemGroup = new ItemGroup(groupName);
			for (String item : items) {
				ItemType type = Sponge.getRegistry().getType(ItemType.class, item).orElse(null);
				if (type == null) {
					String[] split = item.split(";");
					if (split.length > 2) {
						addReservedItemname(split[2]);
						Optional<ItemType> type1 = Sponge.getRegistry().getType(ItemType.class, split[0]);
						if (type1.isPresent()) {
							RPGItemType rpgItemType = new RPGItemType(type1.get(), split[2]);
							itemGroup.getItemTypes().add(rpgItemType);
						}
					}
				} else {
					RPGItemType rpgItemType = new RPGItemType(type, null);
					itemGroup.getItemTypes().add(rpgItemType);
				}
			}
			String damageMultPropertyId = itemGroups.getString("DamageMultPropertyId");
			int idByName = propertyService.getIdByName(damageMultPropertyId);
			itemGroup.setDamageMultPropertyId(idByName);
			addItemGroup(itemGroup);
		}

	}

	public void addItemGroup(ItemGroup itemGroup) {
		itemGroups.put(itemGroup.getGroupName(), itemGroup);
	}

	public ItemGroup getItemGroup(ItemStack itemStack) {
		return getItemGroup(RPGItemType.from(itemStack));
	}

	public ItemGroup getItemGroup(RPGItemType itemType) {
		for (ItemGroup itemGroup : itemGroups.values()) {
			for (RPGItemType rpgItemType : itemGroup.getItemTypes()) {
				if (rpgItemType.getItemType().equals(itemType.getItemType())) {
					if (rpgItemType.getDisplayName() == null && itemType.getDisplayName() == null
							&& rpgItemType.getItemType().equals(itemType.getItemType()))
						return itemGroup;
					if (rpgItemType.getDisplayName() != null &&
							rpgItemType.getDisplayName().equalsIgnoreCase(itemType.getDisplayName()) &&
							rpgItemType.getItemType().equals(itemType.getItemType())
							) {
						return itemGroup;
					}
				}
			}
		}
		return null;
	}

	private void addDefaultItemsToGroup(PrintWriter writer, String id, String damageMultProperty) {
		writer.println("\t{");
		writer.println("\t\tItems:[");
		for (ItemType type : Sponge.getGame().getRegistry().getAllOf(ItemType.class)) {
			if (type.getId().toUpperCase().contains(id)) {
				writer.println("\t\t\t\"" +type.getId() + "\"");
			}
		}
		writer.println("\t\t]");
		writer.println("\t\tItemGroupName:"+id);
		writer.println("\t\tDamageMultPropertyId:"+damageMultProperty);
		writer.println("\t}");
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
				CannotUseItemReson reason = canUse(i, character);
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
		Armor armor = new Armor(itemStack, armorType);
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
					effectService.removeGlobalEffectsAsEnchantments(armor1.getEffects().keySet(), character, armor1);
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
					effectService.removeGlobalEffectsAsEnchantments(armor1.getEffects().keySet(), character, armor1);
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
					effectService.removeGlobalEffectsAsEnchantments(armor1.getEffects().keySet(), character, armor1);
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
					effectService.removeGlobalEffectsAsEnchantments(armor1.getEffects().keySet(), character, armor1);
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
		ItemGroup itemGroup = getItemGroup(is);
		if (itemGroup != null) {
			return buildHotbarWeapon(character, is);
		}
		return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
	}

	private Charm buildCharm(IActiveCharacter character, ItemStack is) {
		Charm charm = new Charm(is);
		charm.setEffects(getItemEffects(is));

		return charm;
	}


	public Weapon buildHotbarWeapon(IActiveCharacter character, ItemStack is) {
		Weapon w = new Weapon(is);

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
		//lore.add(Text.of(LORE_FIRSTLINE, Localization.SKILLBIND));
		is.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.ITALIC, left != null ? left.getName() + " «-" : "", right != null ? "-» " + right.getName() : ""));
		if (right != null) {
			Text text = TextHelper.parse(Localization.CAST_SKILL_ON_RIGHTLICK,
					Arg.arg("skill", right.getName()));
			lore.add(text);
			lore = makeDesc(right, lore);
		}
		if (left != null) {
			Text text = TextHelper.parse(Localization.CAST_SKILl_ON_LEFTCLICK,
					Arg.arg("skill", left.getName()));
			lore.add(text);
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

	protected void changeEquipedWeapon(IActiveCharacter character, Weapon changeTo, ItemStack itemStack) {
		unEquipWeapon(character);

		int slot = ((Hotbar) character.getPlayer().getInventory().query(Hotbar.class)).getSelectedSlotIndex();
		character.setHotbarSlot(slot, changeTo);
		changeTo.current = true;
		changeTo.setSlot(slot);
		if (itemStack == null) {
			changeTo.setEffects(Collections.emptyMap());
			changeTo.setItemType(new RPGItemType());
		} else {
			changeTo.setEffects(getItemEffects(itemStack));
			changeTo.setItemType(RPGItemType.from(itemStack));
		}

		character.setMainHand(changeTo);
		changeTo.onEquip(character);
		damageService.recalculateCharacterWeaponDamage(character, changeTo.getItemType());
	}

	private void unEquipWeapon(IActiveCharacter character) {
		Weapon mainHand = character.getMainHand();
		mainHand.current = false;
		mainHand.onUnEquip(character);
	}
	

	public CannotUseItemReson canWear(ItemStack itemStack, IActiveCharacter character) {
		if (ItemStackUtils.any_armor.contains(itemStack.getType())) {
			if (!character.canWear(RPGItemType.from(itemStack))) {
				return CannotUseItemReson.CONFIG;
			}
		}
		return checkRestrictions(character, itemStack);

	}

	public CannotUseItemReson canUse(ItemStack itemStack, IActiveCharacter character) {
		if (itemStack == null)
			return CannotUseItemReson.OK;

		if (ItemStackUtils.weapons.contains(itemStack.getType())) {
			if (!character.canUse(RPGItemType.from(itemStack))) {
				return CannotUseItemReson.CONFIG;
			}
		} else if (ItemStackUtils.any_armor.contains(itemStack.getType())) {
			if (!character.canWear(RPGItemType.from(itemStack))) {
				return CannotUseItemReson.CONFIG;
			}
		}
		return checkRestrictions(character,itemStack);
	}

	private CannotUseItemReson checkGroupRequirements(IActiveCharacter character, Map<String, Integer> a) {
		if (a.isEmpty())
			return CannotUseItemReson.OK;
		int k = 0;
		Iterator<Map.Entry<String, Integer>> it = a.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> next = it.next();
			Race race = groupService.getRace(next.getKey());
			if (race != null) {
				if (character.getRace() != race) {
					return CannotUseItemReson.LORE;
				}
				if (next.getValue() != null && character.getLevel() < next.getValue()) {
					return CannotUseItemReson.LEVEL;
				}
			} else {
				for (ExtendedNClass extendedNClass : character.getClasses()) {
					if (extendedNClass.getConfigClass().getName().equalsIgnoreCase(next.getKey())) {
						if (next.getValue() != null && character.getLevel() < extendedNClass.getLevel()) {
							return CannotUseItemReson.LEVEL;
						}
						k++;
						continue;
					}
				}
			}
		}
		if (a.size() == k) {
			return CannotUseItemReson.OK;
		} else {
			return CannotUseItemReson.LORE;
		}
	}

	private CannotUseItemReson checkAttributeRequirements(IActiveCharacter character, Map<String, Integer> a) {
		if (a.isEmpty())
			return CannotUseItemReson.OK;
		for (Map.Entry<String, Integer> q : a.entrySet()) {
			ICharacterAttribute attribute = propertyService.getAttribute(q.getKey());
			if (attribute == null)
				continue;
			Integer attributeValue = character.getAttributeValue(attribute);
			if (attributeValue == null || attributeValue < q.getValue())
				return CannotUseItemReson.ATTRIBUTE;

		}
		return CannotUseItemReson.OK;
	}

	private CannotUseItemReson checkRestrictions(IActiveCharacter character, ItemStack is) {
		Optional<Map<String, Integer>> a = is.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS);
		if (a.isPresent()) {
			Map<String, Integer> stringIntegerMap = a.get();
			CannotUseItemReson cannotUseItemReson = checkAttributeRequirements(character, stringIntegerMap);

			if (CannotUseItemReson.OK != cannotUseItemReson) {
				return cannotUseItemReson;
			}
		}
		Optional<Map<String, Integer>> q = is.get(NKeys.ITEM_PLAYER_ALLOWED_GROUPS);
		if (q.isPresent()) {
			Map<String, Integer> w = a.get();
			CannotUseItemReson cannotUseItemReson = checkGroupRequirements(character, w);
			if (CannotUseItemReson.OK != cannotUseItemReson) {
				return cannotUseItemReson;
			}
		}
		return CannotUseItemReson.OK;
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
	public ItemStack setItemRarity(ItemStack itemStack, Text rarity) {
		if (!getItemRarityTypes().contains(rarity.toPlain())) {
			return itemStack;
		}
		itemStack.offer(new ItemRarityData(rarity));
		return updateLore(itemStack);
	}

	public ItemStack setItemLevel(ItemStack itemStack, int level) {
		itemStack.offer(new ItemLevelData(level));
		return updateLore(itemStack);
	}

	public ItemStack updateLore(ItemStack is) {
		ItemLoreBuilderService.ItemLoreBuilder itemLoreBuilder = ItemLoreBuilderService.create(is, new ArrayList<Text>());
		is.offer(Keys.ITEM_LORE, itemLoreBuilder.buildLore());
		is.offer(Keys.HIDE_MISCELLANEOUS, true);
		is.offer(Keys.HIDE_ATTRIBUTES, true);
		return is;
	}


	public Map<IGlobalEffect, EffectParams> getItemEffects(ItemStack is) {
		Optional<Map<String, EffectParams>> q = is.get(NKeys.ITEM_EFFECTS);
		if (q.isPresent()) {
			return getItemEffects(q.get());
		}
		return Collections.emptyMap();
	}

	private Map<IGlobalEffect, EffectParams> getItemEffects(Map<String, EffectParams> stringEffectParamsMap) {
		Map<IGlobalEffect, EffectParams> map = new HashMap<>();
		for (Map.Entry<String, EffectParams> w : stringEffectParamsMap.entrySet()) {
			IGlobalEffect globalEffect = effectService.getGlobalEffect(w.getKey());
			if (globalEffect != null) {
				map.put(globalEffect, w.getValue());
			}
		}
		return map;
	}

	public void addReservedItemname(String k) {
		reservedItemNames.add(k.toLowerCase());
	}

	public Set<String> getReservedItemNames() {
		return reservedItemNames;
	}

	public int getItemLevel(ItemStack itemStack) {
		Optional<Integer> integer = itemStack.get(NKeys.ITEM_LEVEL);
		if (integer.isPresent())
			return integer.get();
		return 0;
	}

	public ItemStack addEffectsToItemStack(ItemStack is, String effectName, EffectParams effectParams) {
		EffectsData effectsData = is.getOrCreate(EffectsData.class).get();
		Optional<Map<String, EffectParams>> q = effectsData.get(NKeys.ITEM_EFFECTS);
		Map<String, EffectParams> w = q.orElse(new HashMap<>());
		w.put(effectName, effectParams);
		effectsData.set(NKeys.ITEM_EFFECTS, w);
		is.offer(effectsData);
		return is;
	}
}
