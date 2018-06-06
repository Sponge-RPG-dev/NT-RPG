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
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.*;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffectSource;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.gui.ItemLoreBuilderService;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.manipulators.*;
import cz.neumimto.rpg.inventory.items.ItemMetaType;
import cz.neumimto.rpg.inventory.items.subtypes.ItemSubtype;
import cz.neumimto.rpg.inventory.items.subtypes.ItemSubtypes;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.inventory.slotparsers.DefaultPlayerInvHandler;
import cz.neumimto.rpg.inventory.slotparsers.PlayerInvHandler;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.reloading.Reload;
import cz.neumimto.rpg.reloading.ReloadService;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.utils.ItemStackUtils;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
public class InventoryService {

    @Inject
	private Logger logger;

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

	@Inject
	private NtRpgPlugin plugin;

	@Inject
	private ItemService itemService;

	private PlayerInvHandler playerInvHandler;

	private Set<String> reservedItemNames = new HashSet<>();

	private Map<String, ItemGroup> itemGroups = new HashMap<>();
	private Map<Class<?>, ManagedInventory> managedInventories = new HashMap<>();

	@Reload(on = ReloadService.PLUGIN_CONFIG)
	@PostProcess(priority = 100)
	public void init() {
		NORMAL_RARITY = Text.of(Localization.NORMAL_RARITY);
		loadItemGroups();
		String s = PluginConfig.EQUIPED_SLOT_RESOLVE_SRATEGY;
		Optional<PlayerInvHandler> type = Sponge.getRegistry().getType(PlayerInvHandler.class, s);
		if (type.isPresent()) {
			playerInvHandler = type.get();
		} else {
			logger.warn("Unknown EQUIPED_SLOT_RESOLVE_SRATEGY, value should be one of " +
					Sponge.getRegistry().getAllOf(PlayerInvHandler.class).stream
					().map(PlayerInvHandler::getId).collect(Collectors.joining(", ")));
			playerInvHandler = IoC.get().build(DefaultPlayerInvHandler.class);
		}
		playerInvHandler.initHandler();
	}

	@Listener
	//Dump items once game started, so we can assume that registries wont change anymore
	public void dumpItems(GameStartedServerEvent event) {
		if (PluginConfig.AUTODISCOVER_ITEMS) {
			Collection<ItemType> allOf = Sponge.getRegistry().getAllOf(ItemType.class);
			ItemDumpConfig itemDump = new ItemDumpConfig();

			for (ItemType itemType : allOf) {
				String id = itemType.getId();
				Field[] fields = itemDump.getClass().getFields();
				for (Field field : fields) {
					if (id.contains(field.getName())) {
						try {
							List<ItemType> itemTypes = (List<ItemType>) field.get(itemDump);
							itemTypes.add(itemType);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						continue;
					}
				}
			}
			try {
				ObjectMapper.BoundInstance configMapper = ObjectMapper.forObject(itemDump);
				File properties = new File(NtRpgPlugin.workingDir, "itemDump.conf");
				if (properties.exists())
					properties.delete();
				HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(properties.toPath()).build();
				SimpleConfigurationNode scn = SimpleConfigurationNode.root();
				configMapper.serialize(scn);
				hcl.save(scn);
			} catch (Exception e) {
				throw new RuntimeException("Could not write file. ", e);
			}


		}
	}

	private void loadItemGroups() {
		Path path = Paths.get(NtRpgPlugin.workingDir+"/ItemGroups.conf");
		File f = path.toFile();
		if (!f.exists()) {
			Optional<Asset> asset = Sponge.getAssetManager().getAsset(plugin, "ItemGroups.conf");
			if (!asset.isPresent()) {
				throw new IllegalStateException("Could not find an asset ItemGroups.conf");
			}
			try {
				asset.get().copyToFile(f.toPath());
			} catch (IOException e) {
				throw new IllegalStateException("Could not create ItemGroups.conf file", e);
			}
		}

		try {
			Config c = ConfigFactory.parseFile(path.toFile());
			reservedItemNames.addAll(c.getStringList("ReservedItemNames"));

			List<String> itemMetaSubtypes = c.getStringList("ItemMetaSubtypes");

			//will break in api 8
			itemMetaSubtypes.stream().map(ItemSubtype::new).forEach(a -> Sponge.getRegistry().register(ItemSubtype.class, a));

			List<? extends Config> inventorySlots = c.getConfigList("InventorySlots");
			for (Config inventorySlot : inventorySlots) {
				loadInventorySettings(inventorySlot);
			}

			List<? extends Config> itemGroups = c.getConfigList("ItemGroups");
			loadItemGroups(itemGroups, null);

			for (String armor : c.getStringList("Armor")) {
				Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, armor);
				if (type.isPresent()) {
					itemService.registerItemArmorType(type.get());
				} else {
					logger.warn(Console.RED + "Could not find item type " + Console.YELLOW + armor + Console.RED + ".");
					logger.warn(Console.RED + " - Is the mod loaded and is the name correct?");
					logger.warn(Console.YELLOW + " - Mod items have to be in the format: " + Console.GREEN+ "\"modid:my_item\"");
				}
			}
			for (String shield : c.getStringList("Shields")) {
				Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, shield);
				if (type.isPresent()) {
					itemService.registerShieldType(type.get());
				} else {
					logger.warn(Console.RED + "Could not find item type " + Console.YELLOW + shield + Console.RED + ".");
					logger.warn(Console.RED + " - Is the mod loaded and is the name correct?");
					logger.warn(Console.YELLOW + " - Mod items have to be in the format: " + Console.GREEN+ "\"modid:my_item\"");
				}
			}
		} catch (ConfigException e) {
			throw new RuntimeException("Could not read ItemGroups.conf ", e);
		}
	}

	private void loadItemGroups(List<? extends Config> itemGroups, WeaponClass parent) {
		for (Config itemGroup : itemGroups) {
			String weaponClass;
			try {
				weaponClass = itemGroup.getString("WeaponClass");
			} catch (ConfigException e) {
				logger.error("Could not read \"WeaponClass\" node, skipping. This is a critical miss configuration, some items will not be recognized as weapons");
				continue;
			}
			logger.info(" - Loading weaponClass" + weaponClass);
			WeaponClass weapons = new WeaponClass(weaponClass);
			weapons.setParent(parent);

			try {
				logger.info("  - Reading \"Items\" config section" + weaponClass);
				List<String> items = itemGroup.getStringList("Items");
				for (String item : items) {
					String[] split = item.split(";");
					Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, split[0]);
					if (!type.isPresent()) {
						logger.warn(Console.RED + "Could not find item type " + Console.YELLOW + split[0] + Console.RED + " defined in ItemGroups.conf.");
						logger.warn(Console.RED + " - Is the mod loaded and is the name correct?");
						logger.warn(Console.YELLOW + " - Mod items have to be in the format: " + Console.GREEN+ "\"modid:my_item\"");
					} else {
						ItemType itemType = type.get();
						String name = null;
						if (split.length > 1) {
							name = split[1];
						}
						itemService.registerItemType(itemType, name, weapons);
					}
				}
			} catch (ConfigException e) {
				try {
					loadItemGroups(itemGroup.getConfigList("Items"), weapons);
				} catch (ConfigException ee) {
					logger.error("Could not read nested configuration for weapon class " + weaponClass + "This is a critical miss configuration, some items will not be recognized as weapons");
				}
			}

			try {
				List<String> properties = itemGroup.getStringList("Properties");
				for (String property : properties) {
					itemService.registerProperty(weapons, property.toLowerCase());
				}
			} catch (ConfigException e) {
				logger.error("Properties configuration section not found, skipping", e);
			}
		}
	}

	private void loadInventorySettings(Config slots) {
		String aClass = slots.getString("type");
		try {
			Class<?> aClass1 = Class.forName(aClass);

			HashMap<Integer, SlotEffectSource> slotEffectSourceHashMap = new HashMap<>();
			ManagedInventory managedInventory = new ManagedInventory(aClass1, slotEffectSourceHashMap);
			for (String str : slots.getStringList("slots")) {
				String[] split = str.split(";");
				if (split.length == 1) {
					SlotEffectSource slotEffectSource = new SlotEffectSource(Integer.parseInt(split[0]), ItemSubtypes.ANY);
					slotEffectSourceHashMap.put(slotEffectSource.getSlotId(), slotEffectSource);
				} else {
					Optional<ItemSubtype> type = Sponge.getRegistry().getType(ItemSubtype.class, split[1]);
					if (!type.isPresent()) {
						type = Optional.of(ItemSubtypes.ANY);
						logger.error("Could not find subtype " + split[1]);
					}
					SlotEffectSource slotEffectSource = new SlotEffectSource(Integer.parseInt(split[0]), type.get());
					slotEffectSourceHashMap.put(slotEffectSource.getSlotId(), slotEffectSource);
				}
			}
			managedInventories.put(managedInventory.getType(), managedInventory);
		} catch (ClassNotFoundException e) {
			logger.error(Console.RED + "Could not find inventory type " + Console.GREEN + aClass + Console.RED + " defined in ItemGroups.conf. Is the mod loaded? Is the class name correct? If you are unsure restart plugin with debug mode ON and interact with desired inventory");
		}
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

	public void initializeCharacterInventory(IActiveCharacter character) {
		if (character.isStub())
			return;
		playerInvHandler.initializeCharacterInventory(character);
	}

	public void dropItem(IActiveCharacter character, ItemStack is, CannotUseItemReason reason) {
		ItemStackUtils.dropItem(character.getPlayer(), is);
		Gui.sendCannotUseItemNotification(character, is, reason);
	}

	//todo event
	public void onRightClick(IActiveCharacter character, int slot, Slot hotbarSlot) {
		if (character.isStub()) {
			return;
		}
		playerInvHandler.onRightClick(character, slot, hotbarSlot);
	}

	public void onLeftClick(IActiveCharacter character, int slot, Slot hotbarSlot) {
		if (character.isStub()) {
			return;
		}
		playerInvHandler.onLeftClick(character, slot, hotbarSlot);
	}

	public CannotUseItemReason canWear(ItemStack itemStack, IActiveCharacter character, RPGItemType type) {
		if (itemStack == null )
			return CannotUseItemReason.OK;
		if (type == null) {
			return CannotUseItemReason.OK; //ItemStack was not recognized as a managed item type. Player may use it
		}
		if (!character.canWear(type)) {
			return CannotUseItemReason.CONFIG;
		}
		return checkRestrictions(character, itemStack);
	}


	public CannotUseItemReason canUse(ItemStack itemStack, IActiveCharacter character, RPGItemType type, HandType h) {
		if (itemStack == null)
			return CannotUseItemReason.OK;
		if (type == null) {
			return CannotUseItemReason.OK; //ItemStack was not recognized as a managed item type. Player may use it
		}
		if (!character.canUse(type, h)) {
			return CannotUseItemReason.CONFIG;
		}
		return checkRestrictions(character,itemStack);
	}

	private CannotUseItemReason checkGroupRequirements(IActiveCharacter character, Map<String, Integer> a) {
		if (a.isEmpty())
			return CannotUseItemReason.OK;
		int k = 0;
		Iterator<Map.Entry<String, Integer>> it = a.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> next = it.next();
			Race race = groupService.getRace(next.getKey());
			if (race != null) {
				if (character.getRace() != race) {
					return CannotUseItemReason.LORE;
				}
				if (next.getValue() != null && character.getLevel() < next.getValue()) {
					return CannotUseItemReason.LEVEL;
				}
			} else {
				for (ExtendedNClass extendedNClass : character.getClasses()) {
					if (extendedNClass.getConfigClass().getName().equalsIgnoreCase(next.getKey())) {
						if (next.getValue() != null && character.getLevel() < extendedNClass.getLevel()) {
							return CannotUseItemReason.LEVEL;
						}
						k++;
					}
				}
			}
		}
		if (a.size() == k) {
			return CannotUseItemReason.OK;
		} else {
			return CannotUseItemReason.LORE;
		}
	}

	private CannotUseItemReason checkAttributeRequirements(IActiveCharacter character, Map<String, Integer> a) {
		if (a.isEmpty())
			return CannotUseItemReason.OK;
		for (Map.Entry<String, Integer> q : a.entrySet()) {
			ICharacterAttribute attribute = propertyService.getAttribute(q.getKey());
			if (attribute == null)
				continue;
			Integer attributeValue = character.getAttributeValue(attribute);
			if (attributeValue == null || attributeValue < q.getValue())
				return CannotUseItemReason.ATTRIBUTE;

		}
		return CannotUseItemReason.OK;
	}

	private CannotUseItemReason checkRestrictions(IActiveCharacter character, DataHolder is) {
		Optional<Map<String, Integer>> a = is.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS);
		if (a.isPresent()) {
			Map<String, Integer> stringIntegerMap = a.get();
			CannotUseItemReason cannotUseItemReason = checkAttributeRequirements(character, stringIntegerMap);

			if (CannotUseItemReason.OK != cannotUseItemReason) {
				return cannotUseItemReason;
			}
		}
		Optional<Map<String, Integer>> q = is.get(NKeys.ITEM_PLAYER_ALLOWED_GROUPS);
		if (q.isPresent()) {
			Map<String, Integer> w = q.get();
			CannotUseItemReason cannotUseItemReason = checkGroupRequirements(character, w);
			if (CannotUseItemReason.OK != cannotUseItemReason) {
				return cannotUseItemReason;
			}
		}
		return CannotUseItemReason.OK;
	}

	/**
	 *
	 * @param slot clicked slot
	 * @param player who clicked a slot
	 * @return true to cancell the event
	 */
	public boolean processSlotInteraction(Slot slot, Player player) {
		IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
		return character.isStub() || playerInvHandler.processSlotInteraction(character, slot);
	}

	public void processHotbarItemDispense(Player player) {
		IActiveCharacter character = characterService.getCharacter(player);
		if (character.isStub())
			return;
		playerInvHandler.processHotbarItemDispense(character);
	}

	public ItemStack setItemLevel(ItemStack itemStack, int level) {
		itemStack.offer(new ItemLevelData(level));
		return updateLore(itemStack);
	}

	public ItemStack updateLore(ItemStack is) {
		ItemLoreBuilderService.ItemLoreBuilder itemLoreBuilder = ItemLoreBuilderService.create(is, new ArrayList<>());
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
		return integer.orElse(0);
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

	public void createItemMetaSectionIfMissing(ItemStack itemStack) {
		Optional<Text> text = itemStack.get(NKeys.ITEM_META_HEADER);
		if (!text.isPresent()) {
			itemStack.offer(new ItemMetaHeader(TextHelper.parse("&3Meta")));
		}
	}

	public void setItemRarity(ItemStack itemStack, Integer integer) {
		Optional<ItemRarityData> orCreate = itemStack.getOrCreate(ItemRarityData.class);
		ItemRarityData itemRarityData = orCreate.get();
		itemRarityData.set(NKeys.ITEM_RARITY, integer);
		itemStack.offer(itemRarityData);
	}

	public void createItemMeta(ItemStack itemStack, Text meta) {
		Optional<ItemMetaHeader> orCreate = itemStack.getOrCreate(ItemMetaHeader.class);
		ItemMetaHeader data = orCreate.get();
		data.set(NKeys.ITEM_META_HEADER, meta);
		itemStack.offer(data);
	}

	public void setItemRestrictions(ItemStack itemStack, Map<String, Integer> classReq, Map<String, Integer> attrreq) {
		Optional<MinimalItemRequirementsData> orCreate = itemStack.getOrCreate(MinimalItemRequirementsData.class);
		MinimalItemRequirementsData data = orCreate.get();
		data.set(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS, attrreq);
		data.set(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, classReq);
		itemStack.offer(data);
	}

	public void addGroupRestriction(ItemStack itemStack, PlayerGroup clazz, int level) {
		Optional<MinimalItemGroupRequirementsData> orCreate = itemStack.getOrCreate(MinimalItemGroupRequirementsData.class);
		MinimalItemGroupRequirementsData data = orCreate.get();
		Map<String, Integer> map = data.get(NKeys.ITEM_PLAYER_ALLOWED_GROUPS).orElse(new HashMap<>());
		map.put(clazz.getName(), level);
		data.set(NKeys.ITEM_PLAYER_ALLOWED_GROUPS, map);
		itemStack.offer(data);
	}

	public void setItemMetaType(ItemStack itemStack, ItemMetaType metaType) {
		ItemMetaTypeData orCreate = itemStack.getOrCreate(ItemMetaTypeData.class).orElse(new ItemMetaTypeData(metaType));
		orCreate.set(NKeys.ITEM_META_TYPE, metaType);
		itemStack.offer(orCreate);
	}


	public IEffectSource getEffectSourceBySlotId(Class<?> type, Integer value) {
		ManagedInventory managedInventory = managedInventories.get(type);
		if (managedInventory == null)
			return null;
		return managedInventory.getSlotEffectSourceHashMap().get(value);
	}

	public IEffectSource getEffectSourceBySlotId(Slot slot) {
		Slot transform = slot.transform();
		Class type = transform.parent().getClass();
		SlotIndex index = slot.getInventoryProperty(SlotIndex.class).get();
		ManagedInventory managedInventory = managedInventories.get(type);
		if (managedInventory == null)
			return null;
		return managedInventory.getSlotEffectSourceHashMap().get(index.getValue());
	}

	public ManagedInventory getManagedInventory(Class<?> type) {
		return managedInventories.get(type);
	}

	/**
	 *
	 * @param player
	 * @param futureMainHand
	 * @param futureOffHand
	 * @return True if the swap hand event shall be cancelled
	 */
	public boolean processHotbarSwapHand(Player player, ItemStack futureMainHand, ItemStack futureOffHand) {
		IActiveCharacter character = characterService.getCharacter(player);
		if (character.isStub())
			return true;
		return playerInvHandler.processHotbarSwapHand(character, futureMainHand, futureOffHand);
	}

	public ItemStack createSkillbind(ISkill iSkill) {
		ItemStack itemStack = ItemStack.of(ItemTypes.PUMPKIN_SEEDS, 1);
		SkillBindData orCreate = itemStack.getOrCreate(SkillBindData.class).orElse(new SkillBindData(iSkill.getName()));
		orCreate.set(NKeys.SKILLBIND, iSkill.getName());
		itemStack.offer(Keys.DISPLAY_NAME, Text.of(iSkill.getName()));
		itemStack.offer(orCreate);
		return itemStack;
	}
}
