package cz.neumimto.rpg.gui;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.commands.InfoCommand;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.data.InventoryCommandItemMenuData;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.SkillTreeInventoryViewControllsData;
import cz.neumimto.rpg.listeners.SkillTreeInventoryListener;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.SkillTreeViewModel;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.skills.tree.SkillTree;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.AcceptsItems;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

import static cz.neumimto.rpg.gui.CatalogTypeItemStackBuilder.Block;
import static cz.neumimto.rpg.gui.CatalogTypeItemStackBuilder.Item;

/**
 * Created by ja on 29.12.2016.
 */
public class GuiHelper {

	public static Map<DamageType, CatalogTypeItemStackBuilder> damageTypeToItemStack = new HashMap<>();
	private static NtRpgPlugin plugin;

	static {
		plugin = IoC.get().build(NtRpgPlugin.class);

		damageTypeToItemStack.put(DamageTypes.ATTACK, Item.of(ItemTypes.STONE_SWORD));
		damageTypeToItemStack.put(DamageTypes.CONTACT, Item.of(ItemTypes.CACTUS));

		damageTypeToItemStack.put(DamageTypes.CUSTOM, Item.of(ItemTypes.BARRIER));

		damageTypeToItemStack.put(DamageTypes.DROWN, Block.of(BlockTypes.WATER));
		damageTypeToItemStack.put(DamageTypes.EXPLOSIVE, Item.of(ItemTypes.TNT));
		damageTypeToItemStack.put(DamageTypes.FALL, Item.of(ItemTypes.IRON_BOOTS));
		damageTypeToItemStack.put(DamageTypes.FIRE, Item.of(ItemTypes.BLAZE_POWDER));

		damageTypeToItemStack.put(DamageTypes.HUNGER, Item.of(ItemTypes.ROTTEN_FLESH));
		damageTypeToItemStack.put(DamageTypes.MAGMA, Block.of(BlockTypes.LAVA));
		damageTypeToItemStack.put(DamageTypes.PROJECTILE, Item.of(ItemTypes.TIPPED_ARROW));
		damageTypeToItemStack.put(DamageTypes.VOID, Block.of(BlockTypes.PORTAL));
		damageTypeToItemStack.put(DamageTypes.MAGIC, Item.of(ItemTypes.ENCHANTED_BOOK));

		damageTypeToItemStack.put(NDamageType.ICE, Block.of(BlockTypes.ICE));
		damageTypeToItemStack.put(NDamageType.MAGICAL, Item.of(ItemTypes.ENCHANTED_BOOK));
		damageTypeToItemStack.put(NDamageType.MEELE_CRITICAL, Item.of(ItemTypes.DIAMOND_SWORD));
		damageTypeToItemStack.put(NDamageType.PHYSICAL, Item.of(ItemTypes.IRON_NUGGET));
		damageTypeToItemStack.put(NDamageType.LIGHTNING, Item.of(ItemTypes.NETHER_STAR));
	}

	public static ItemStack itemStack(ItemType type) {
		ItemStack is = ItemStack.of(type, 1);
		is.offer(Keys.HIDE_ATTRIBUTES, true);
		is.offer(Keys.HIDE_MISCELLANEOUS, true);
		return is;
	}

	public static ItemStack damageTypeToItemStack(DamageType type) {
		if (type == null) {
			return itemStack(ItemTypes.STONE);
		}
		CatalogTypeItemStackBuilder a = damageTypeToItemStack.get(type);
		ItemStack is = null;
		if (a == null) {
			is = itemStack(ItemTypes.STONE);
		} else {
			is = a.toItemStack();
		}
		is.offer(new MenuInventoryData(true));
		is.offer(Keys.DISPLAY_NAME, Text.of(type.getName()));
		return is;
	}

	public static Inventory createPlayerGroupView(ClassDefinition group) {
		Inventory.Builder builder = Inventory
				.builder();
		Inventory i = builder.of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryTitle.of(Text.of(group.getName(), group.getPreferedColor(), TextStyles.BOLD)))
				.build(plugin);


		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 2))).offer(createWeaponCommand(group));
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 2))).offer(createArmorCommand(group));
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(2, 3))).offer(createAttributesCommand(group));
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(3, 3))).offer(createPropertyCommand(group));
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 3))).offer(toItemStack(group));
		return i;
	}

	private static ItemStack createPropertyCommand(ClassDefinition group) {
		ItemStack i = itemStack(ItemTypes.BOOK);
		i.offer(NKeys.MENU_INVENTORY, true);
		i.offer(Keys.DISPLAY_NAME, Localizations.ATTRIBUTES.toText());
		String cc = IoC.get().build(InfoCommand.class).getAliases().iterator().next();
		i.offer(new InventoryCommandItemMenuData(cc + " properties-initial " + group.getName()));
		return i;
	}

	public static ItemStack createAttributesCommand(ClassDefinition group) {
		ItemStack i = itemStack(ItemTypes.BOOK);
		i.offer(NKeys.MENU_INVENTORY, true);
		i.offer(Keys.DISPLAY_NAME, Localizations.ATTRIBUTES.toText());
		String cc = IoC.get().build(InfoCommand.class).getAliases().iterator().next();
		i.offer(new InventoryCommandItemMenuData(cc + " attributes-initial " + group.getName()));
		return i;
	}

	public static ItemStack createDescriptionItem(String description) {
		ItemStack i = itemStack(ItemTypes.PAPER);
		i.offer(Keys.DISPLAY_NAME, Text.of(""));
		i.offer(Keys.HIDE_MISCELLANEOUS, true);
		i.offer(Keys.HIDE_ATTRIBUTES, true);
		i.offer(new MenuInventoryData(true));
		i.offer(Keys.ITEM_LORE, Collections.singletonList(Text.of(description, TextColors.GRAY)));
		return i;
	}

	public static ItemStack createArmorCommand(ClassDefinition group) {
		ItemStack i = itemStack(ItemTypes.DIAMOND_CHESTPLATE);
		i.offer(NKeys.MENU_INVENTORY, true);
		i.offer(Keys.DISPLAY_NAME, Localizations.ARMOR.toText());
		i.offer(Keys.ITEM_LORE, Collections.singletonList(Localizations.ARMOR_MENU_HELP.toText()));
		i.offer(new InventoryCommandItemMenuData("armor " + group.getName()));
		return i;
	}

	public static ItemStack createWeaponCommand(ClassDefinition group) {
		ItemStack i = itemStack(ItemTypes.DIAMOND_SWORD);
		i.offer(NKeys.MENU_INVENTORY, true);
		i.offer(Keys.DISPLAY_NAME, Localizations.WEAPONS.toText());
		i.offer(Keys.ITEM_LORE, Collections.singletonList(Localizations.WEAPONS_MENU_HELP.toText()));
		i.offer(new InventoryCommandItemMenuData("weapons " + group.getName()));
		return i;
	}

	public static ItemStack propertyToItemStack(int id, float value) {
		ItemStack i = itemStack(ItemTypes.BOOK);
		String nameById = NtRpgPlugin.GlobalScope.propertyService.getNameById(id);
		nameById = Utils.configNodeToReadableString(nameById);
		i.offer(Keys.DISPLAY_NAME, TextHelper.makeText(nameById, TextColors.GREEN));
		if (nameById.endsWith("mult")) {
			i.offer(Keys.ITEM_LORE, Collections.singletonList(TextHelper.makeText((value * 100) + "%", TextColors.GOLD)));
		} else {
			i.offer(Keys.ITEM_LORE, Collections.singletonList(TextHelper.makeText(String.valueOf(value), TextColors.GOLD)));
		}
		i.offer(new MenuInventoryData(true));
		return i;
	}

	public static List<Text> getItemLore(String s) {
		String[] a = s.split("\\n");
		List<Text> t = new ArrayList<>();
		for (String s1 : a) {
			t.add(Text.builder(s1).color(TextColors.GOLD).style(TextStyles.ITALIC).build());
		}
		return t;
	}

	public static ItemStack back(String command, Text displayName) {
		ItemStack of = itemStack(ItemTypes.PAPER);
		of.offer(Keys.DISPLAY_NAME, displayName);
		of.offer(new InventoryCommandItemMenuData(command));
		return of;
	}

	public static ItemStack unclickableInterface() {
		ItemStack of = itemStack(ItemTypes.STAINED_GLASS_PANE);
		of.offer(new MenuInventoryData(true));
		of.offer(Keys.DYE_COLOR, DyeColors.YELLOW);
		of.offer(Keys.DISPLAY_NAME, Text.EMPTY);
		return of;
	}

	public static ItemStack back(ClassDefinition g) {
		ItemStack of = itemStack(ItemTypes.PAPER);
		of.offer(Keys.DISPLAY_NAME, Localizations.BACK.toText());
		of.offer(new InventoryCommandItemMenuData(g.getClassType() + " " + g.getName()));
		return of;
	}

	public static ItemStack skillToItemStack(IActiveCharacter character, SkillData skillData, SkillTree skillTree) {
		return skillData.getSkill().toItemStack(character, skillData, skillTree);
	}


	public static Inventory createSkillTreeInventoryViewTemplate(IActiveCharacter character, SkillTree skillTree) {
		Inventory i = Inventory.builder()
				.of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryTitle.of(Localizations.SKILLTREE.toText()))
				.property(AcceptsItems.of(Collections.EMPTY_LIST))
				.listener(ClickInventoryEvent.Primary.class,
						event -> new SkillTreeInventoryListener().onOptionSelect(event, (Player) event.getCause().root()))
				.listener(ClickInventoryEvent.Secondary.class,
						event -> new SkillTreeInventoryListener().onOptionSelect(event, (Player) event.getCause().root()))
				.build(plugin);

		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 0))).offer(unclickableInterface());
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 1))).offer(unclickableInterface());
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 2))).offer(unclickableInterface());
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 3))).offer(unclickableInterface());
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 4))).offer(unclickableInterface());
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(7, 5))).offer(unclickableInterface());


		SkillTreeViewModel model = character.getSkillTreeViewLocation().get(skillTree.getId());

		ItemStack md = interactiveModeToitemStack(character, model.getInteractiveMode());
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 1))).set(md);

		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 2))).offer(createControlls(SkillTreeControllsButton.NORTH));
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 3))).offer(createControlls(SkillTreeControllsButton.SOUTH));
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 4))).offer(createControlls(SkillTreeControllsButton.WEST));
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 5))).offer(createControlls(SkillTreeControllsButton.EAST));

		return i;
	}

	public static ItemStack createControlls(SkillTreeControllsButton button) {
		ItemStack itemStack = VanillaMessaging.controlls.get(button).toItemStack();
		itemStack.offer(new SkillTreeInventoryViewControllsData(button));
		itemStack.offer(new MenuInventoryData(true));
		return itemStack;
	}

	public static ItemStack createSkillTreeInventoryMenuBoundary() {
		ItemStack of = itemStack(ItemTypes.STAINED_GLASS_PANE);
		of.offer(Keys.DISPLAY_NAME, Text.EMPTY);
		of.offer(new MenuInventoryData(true));
		of.offer(Keys.DYE_COLOR, DyeColors.RED);
		return of;
	}

	public static ItemStack createSkillTreeConfirmButtom() {
		ItemStack itemStack = itemStack(ItemTypes.KNOWLEDGE_BOOK);
		itemStack.offer(Keys.DISPLAY_NAME, Localizations.CONFIRM_SKILL_SELECTION_BUTTON.toText());
		itemStack.offer(new SkillTreeInventoryViewControllsData(SkillTreeControllsButton.CONFIRM));
		return itemStack;
	}

	public static Inventory createSkillDetailInventoryView(IActiveCharacter character, SkillTree skillTree, SkillData skillData) {
		Inventory build = Inventory.builder()
				.of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryTitle.of(skillData.getSkill().getLocalizableName().toBuilder().color(TextColors.DARK_GREEN).style(TextStyles.BOLD)
						.build()))
				.build(plugin);

		ItemStack back = back("skilltree " + character.getLastTimeInvokedSkillTreeView().getViewedClass().getName(), Localizations.SKILLTREE.toText());
		build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(back);

		if (skillData instanceof SkillPathData) {
			SkillPathData data = (SkillPathData) skillData;

			ItemStack of = itemStack(ItemTypes.PAPER);
			of.offer(Keys.DISPLAY_NAME, Text.of("Tier " + data.getTier()));
			of.offer(new MenuInventoryData(true));
			build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 0))).offer(of);

			SkillService skillService = IoC.get().build(SkillService.class);

			int i = 0;
			int j = 2;
			for (Map.Entry<String, Integer> entry : data.getSkillBonus().entrySet()) {
				ISkill skill = skillService.getById(entry.getKey()).orElse(null);
				if (skill != null) {
					ItemStack itemStack = skillToItemStack(character, character.getSkill(skill.getId()).getSkillData(), skillTree);
					itemStack.offer(Keys.DISPLAY_NAME, Text
							.builder(String.format("%+d", entry.getValue()) + " | " + entry.getKey())
							.color(entry.getValue() < 0 ? TextColors.RED : TextColors.DARK_GREEN)
							.build());
					build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(j, i))).offer(itemStack);
					if (j > 8) {
						j = 0;
						i++;
					} else {
						j++;
					}
				}
			}

		} else {
			DamageType type = skillData.getSkill().getDamageType();
			if (type != null) {
				build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(1, 1))).offer(damageTypeToItemStack(type));
			}

			List<ItemStack> itemStacks = skillData.getSkill().configurationToItemStacks(skillData);
			int m, n, i = 0;

			for (m = 0; m < 8; m++) {
				for (n = 3; n < 5; n++) {
					if (i > itemStacks.size() - 1) {
						return build;
					}
					build.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(m, n))).offer(itemStacks.get(i));
					i++;
				}
			}
		}
		return build;

	}

	public static ItemStack interactiveModeToitemStack(IActiveCharacter character, SkillTreeViewModel.InteractiveMode interactiveMode) {
		ItemStack md = itemStack(interactiveMode.getItemType());
		List<Text> lore = new ArrayList<>();

		md.offer(new SkillTreeInventoryViewControllsData(SkillTreeControllsButton.MODE));
		md.offer(new MenuInventoryData(true));
		lore.add(interactiveMode.getTransltion());
		lore.add(Text.EMPTY);
		lore.add(Text.builder("Level: ").color(TextColors.YELLOW)
				.append(Text.builder(String.valueOf(character.getLevel())).style(TextStyles.BOLD).build())
				.build());

		int sp = character.getCharacterBase().getCharacterClass(character.getPrimaryClass().getClassDefinition()).getSkillPoints();

		lore.add(Text.builder("SP: ").color(TextColors.GREEN)
				.append(Text.builder(String.valueOf(sp)).style(TextStyles.BOLD).build())
				.build());
		md.offer(Keys.ITEM_LORE, lore);
		return md;
	}

	public static ItemStack rpgItemTypeToItemStack(ConfigRPGItemType configRPGItemType) {
		ItemStack q = itemStack(configRPGItemType.getRpgItemType().getItemType());
		Text lore = Text.builder().append(Localizations.ITEM_DAMAGE.toText())
				.append(Text.builder(": " + configRPGItemType.getDamage())
						.style(TextStyles.BOLD)
						.color(NtRpgPlugin.GlobalScope.damageService.getColorByDamage(configRPGItemType.getDamage()))
						.build())
				.build();
		q.offer(Keys.ITEM_LORE, Collections.singletonList(lore));
		q.offer(new MenuInventoryData(true));
		if (configRPGItemType.getRpgItemType().getDisplayName() != null) {
			q.offer(Keys.DISPLAY_NAME, Text.of(configRPGItemType.getRpgItemType().getDisplayName()));
		}
		return q;
	}

	public static ItemStack toItemStack(ClassDefinition a) {
		ItemStack itemStack = itemStack(a.getItemType());
		itemStack.offer(Keys.DISPLAY_NAME, Text.of(a.getName(), a.getPreferedColor()));

		if (a.getCustomLore().isEmpty()) {
			itemStack.offer(Keys.ITEM_LORE, a.getCustomLore());
		} else {
			List<Text> lore = new ArrayList<>();
			String description = a.getDescription();
			lore.add(Text.of(a.getClassType(), TextStyles.BOLD, TextColors.GRAY));
			lore.add(Text.EMPTY);
			lore.add(Text.of(description, TextStyles.ITALIC, TextColors.GOLD));
			itemStack.offer(Keys.ITEM_LORE, lore);
		}

		return itemStack;
	}
}
