package cz.neumimto.rpg.gui;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.commands.InfoCommand;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.EffectSourceType;
import cz.neumimto.rpg.inventory.data.InventoryCommandItemMenuData;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.skills.ActiveSkill;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.PassiveSkill;
import cz.neumimto.rpg.skills.SkillData;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ja on 29.12.2016.
 */
public class GuiHelper {


	private static NtRpgPlugin plugin;

	static {
		plugin = IoC.get().build(NtRpgPlugin.class);
	}

	public static Inventory createPlayerGroupView(PlayerGroup group) {
		Inventory.Builder builder = Inventory.builder();
		Inventory i = builder.of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
		i.query(new SlotPos(2, 2)).offer(createWeaponCommand(group));
		i.query(new SlotPos(3, 2)).offer(createArmorCommand(group));
		i.query(new SlotPos(2, 3)).offer(createAttributesCommand(group));
		i.query(new SlotPos(0, 0)).offer(createDescriptionItem(group.getDescription()));
		return i;
	}

	public static ItemStack createAttributesCommand(PlayerGroup group) {
		ItemStack i = ItemStack.of(ItemTypes.BOOK, 1);
		i.offer(NKeys.MENU_INVENTORY, true);
		i.offer(Keys.DISPLAY_NAME, Text.of(Localization.ATTRIBUTES, TextColors.DARK_RED));
		String cc = IoC.get().build(InfoCommand.class).getAliases().iterator().next();
		i.offer(new InventoryCommandItemMenuData(cc + " attributes-initial " + group.getName()));
		return i;
	}

	public static ItemStack createDescriptionItem(String description) {
		ItemStack i = ItemStack.of(ItemTypes.PAPER, 1);
		i.offer(Keys.DISPLAY_NAME, Text.of(""));
		i.offer(Keys.HIDE_MISCELLANEOUS, true);
		i.offer(Keys.HIDE_ATTRIBUTES, true);
		i.offer(new MenuInventoryData(true));
		i.offer(Keys.ITEM_LORE, Collections.singletonList(Text.of(description, TextColors.GRAY)));
		return i;
	}

	public static ItemStack createArmorCommand(PlayerGroup group) {
		ItemStack i = ItemStack.of(ItemTypes.DIAMOND_CHESTPLATE, 1);
		i.offer(NKeys.MENU_INVENTORY, true);
		i.offer(Keys.DISPLAY_NAME, Text.of(Localization.WEAPONS, TextColors.DARK_RED));
		i.offer(Keys.ITEM_LORE, Collections.singletonList(Text.of(Localization.WEAPONS_MENU_HELP, TextColors.GRAY)));
		i.offer(Keys.HIDE_MISCELLANEOUS, true);
		i.offer(Keys.HIDE_ATTRIBUTES, true);
		i.offer(new InventoryCommandItemMenuData("show armor " + group.getName()));
		return i;
	}

	public static ItemStack createWeaponCommand(PlayerGroup group) {
		ItemStack i = ItemStack.of(ItemTypes.DIAMOND_SWORD, 1);
		i.offer(NKeys.MENU_INVENTORY, true);
		i.offer(Keys.DISPLAY_NAME, Text.of(Localization.ARMOR, TextColors.DARK_RED));
		i.offer(Keys.ITEM_LORE, Collections.singletonList(Text.of(Localization.ARMOR_MENU_HELP, TextColors.GRAY)));
		i.offer(Keys.HIDE_MISCELLANEOUS, true);
		i.offer(Keys.HIDE_ATTRIBUTES, true);
		i.offer(new InventoryCommandItemMenuData("show weapons " + group.getName()));
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

	public static ItemStack back(String command, String displayName) {
		ItemStack of = ItemStack.of(ItemTypes.PAPER, 1);
		of.offer(Keys.DISPLAY_NAME, Text.of(displayName, TextColors.WHITE));
		of.offer(new InventoryCommandItemMenuData(command));
		return of;
	}

	public static ItemStack back(PlayerGroup g) {
		ItemStack of = ItemStack.of(ItemTypes.PAPER, 1);
		String l = "class";
		if (g.getType() == EffectSourceType.RACE) {
			l = "race";
		}
		of.offer(Keys.DISPLAY_NAME, Text.of(Localization.BACK, TextColors.WHITE));
		String c = IoC.get().build(InfoCommand.class).getAliases().get(0);
		of.offer(new InventoryCommandItemMenuData(c + " " + l + " " + g.getName()));
		return of;
	}

	public static ItemStack skillToItemStack(IActiveCharacter character, SkillData skillData) {
		ISkill skill = skillData.getSkill();
		ItemType itemType = skill.getIcon().itemType;
		if (itemType == null) {
			itemType = ItemTypes.STONE;
		}

		ItemStack.Builder builder = ItemStack.builder();
		builder.itemType(itemType);
		List<Text> lore = new ArrayList<>();

		String desc = skill.getDescription();
		String skillTargetType = "Targetted";
		if (skill instanceof ActiveSkill) {
			skillTargetType = "Active";
		} else if (skill instanceof PassiveSkill) {
			skillTargetType = "Passive";
		}
		lore.add(Text.of(desc, TextColors.GOLD));
		lore.add(Text.of(skillTargetType, TextColors.DARK_PURPLE, TextStyles.ITALIC));
		lore.add(Text.EMPTY);

		int minPlayerLevel = skillData.getMinPlayerLevel();
		int maxSkillLevel = skillData.getMaxSkillLevel();

		String s = "Min. Player lvl: ";
		if (minPlayerLevel > 0) {
			lore.add(Text.builder(s).color(TextColors.YELLOW)
					.append(Text.builder("" + minPlayerLevel)
							.color(character.getLevel() < minPlayerLevel ? TextColors.RED : TextColors.GREEN)
							.build())
					.build());
		}
		s = "Max. Skill lvl: " + maxSkillLevel;
		lore.add(Text.builder(s)
				.color(TextColors.YELLOW)
				.build());
		lore.add(Text.EMPTY);


		if (skill.getLore() != null) {
			lore.add(Text.builder(skill.getLore()).style(TextStyles.ITALIC).color(TextColors.GOLD).build());
		}
		ItemStack is = ItemStack.builder().itemType(itemType)
				.quantity(1)
				.add(Keys.ITEM_LORE, lore)
				.build();
		is.offer(new MenuInventoryData(true));
		return is;
	}
}
