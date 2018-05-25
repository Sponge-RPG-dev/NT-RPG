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

package cz.neumimto.rpg.gui;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.*;
import cz.neumimto.rpg.commands.InfoCommand;
import cz.neumimto.rpg.configuration.CommandPermissions;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.effects.*;
import cz.neumimto.rpg.effects.common.def.BossBarExpNotifier;
import cz.neumimto.rpg.effects.common.def.ManaBarNotifier;
import cz.neumimto.rpg.inventory.CannotUseItemReson;
import cz.neumimto.rpg.inventory.ConfigRPGItemType;
import cz.neumimto.rpg.inventory.RPGItemType;
import cz.neumimto.rpg.inventory.data.InventoryCommandItemMenuData;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.inventory.data.SkillTreeInventoryViewControllsData;
import cz.neumimto.rpg.inventory.data.manipulators.SkillTreeNode;
import cz.neumimto.rpg.inventory.runewords.ItemUpgrade;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import cz.neumimto.rpg.persistance.DirectAccessDao;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.*;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.reloading.Reload;
import cz.neumimto.rpg.reloading.ReloadService;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.SkillTree;
import cz.neumimto.rpg.utils.ItemStackUtils;
import cz.neumimto.rpg.utils.SkillTreeActionResult;
import cz.neumimto.rpg.utils.Utils;
import cz.neumimto.rpg.utils.model.CharacterListModel;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Color;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static cz.neumimto.rpg.gui.GuiHelper.*;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
public class VanillaMessaging implements IPlayerMessage {

	@Inject
	private Game game;

	@Inject
	private GroupService groupService;

	@Inject
	private EffectService effectService;

	@Inject
	private NtRpgPlugin plugin;

	@Inject
	private RWService rwService;

	@Inject
	private InfoCommand infoCommand;

	@Inject
	private DamageService damageService;

	@Inject
	private CharacterService characterService;

	@Inject
	private SkillService skillService;


	public static Map<SkillTreeControllsButton, SkillTreeInterfaceModel> controlls;

	@Reload(on = ReloadService.PLUGIN_CONFIG)
	@PostProcess
	public void load() {
		controlls = new HashMap<>();
		for (String a : PluginConfig.SKILLTREE_BUTTON_CONTROLLS) {
			String[] split = a.split(",");

			SkillTreeControllsButton key = SkillTreeControllsButton.valueOf(split[0].toUpperCase());
			ItemType type = Sponge.getRegistry().getType(ItemType.class, split[1]).orElse(ItemTypes.BARRIER);

			controlls.put(key, new SkillTreeInterfaceModel(Integer.parseInt(split[3]),type,split[2], (short)0));

		}
	}


	@Override
	public boolean isClientSideGui() {
		return false;
	}

	@Override
	public void invokerDefaultMenu(IActiveCharacter character) {

	}

	@Override
	public void sendMessage(IActiveCharacter player, String message) {
		player.sendMessage(message);
	}


	private static final String timeleft = "tl";
	private static final TextTemplate.Arg TIMELEFT = TextTemplate.arg("tl")
			.color(TextColors.WHITE)
			.style(TextStyles.BOLD)
			.build();

	private static final String skillname = "sk";
	private static final TextTemplate.Arg SKILLNAME = TextTemplate.arg(skillname)
			.color(TextColors.WHITE)
			.style(TextStyles.BOLD)
			.build();

	@Override
	public void sendCooldownMessage(IActiveCharacter player, String message, double cooldown) {
		player.getPlayer().sendMessage(TextHelper.parse(Localization.ON_COOLDOWN, Arg.arg("skill", message).with("time", cooldown)));
	}

	@Override
	public void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {
		sendMessage(player, type.toMessage(effect));
	}

	@Override
	public void invokeCharacterMenu(Player player, List<CharacterBase> characterBases) {
		ItemStack.Builder b = ItemStack.builder();
		List<ItemStack> list = new ArrayList<>();
		//todo
	}


	@Override
	public void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target) {
		PaginationService paginationService = game.getServiceManager().provide(PaginationService.class).get();
		PaginationList.Builder builder = paginationService.builder();
		builder.padding(Text.builder("=").color(TextColors.GREEN).build());
		List<Text> content = new ArrayList<>();
		for (CharacterBase characterBase : target) {
			String name = characterBase.getName();
			int level = character.getPrimaryClass().getLevel();
			String name1 = character.getRace().getName();
			Text.Builder b = Text.builder();
			b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
					.append(Text.builder("SELECT").color(TextColors.GREEN).build())
					.append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
			b.append(Text.of(name));
			if (character.getPrimaryClass() != ExtendedNClass.Default) {
				b.append(Text.builder(" ").build()).append(Text.of(level));
			}
			if (character.getRace() != Race.Default) {
				b.append(Text.of(name1));
			}
			content.add(b.build());
		}
		builder.contents(content);
		builder.sendTo(character.getPlayer());
	}


	private String getDetailedCharInfo(IActiveCharacter character) {
		Text text = Text.builder("Level").color(TextColors.YELLOW).append(
				Text.builder("Race").color(TextColors.RED).append(
						Text.builder("Guild").color(TextColors.AQUA).append(
								Text.builder("Class").color(TextColors.GOLD).build()
						).build()).build()).build();
		return text.toString();
	}

	@Override
	public void sendPlayerInfo(IActiveCharacter character, IActiveCharacter target) {
		character.sendMessage(getDetailedCharInfo(target));
	}

	@Override
	public void showExpChange(IActiveCharacter character, String classname, double expchange) {
		IEffectContainer<Object, BossBarExpNotifier> barExpNotifier = character.getEffect(BossBarExpNotifier.name);
		BossBarExpNotifier effect = (BossBarExpNotifier) barExpNotifier;
		if (effect == null) {
			effect = new BossBarExpNotifier(character);
			effectService.addEffect(effect, character, InternalEffectSourceProvider.INSTANCE);
		}
		effect.notifyExpChange(character, classname, expchange);
	}

	@Override
	public void showLevelChange(IActiveCharacter character, ExtendedNClass clazz, int level) {
		Player player = character.getPlayer();
		player.sendMessage(Text.of("Level up: " + clazz.getConfigClass().getName() + " - " + level));
	}

	@Override
	public void sendStatus(IActiveCharacter character) {
		CharacterBase base = character.getCharacterBase();

		PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
		PaginationList.Builder builder = paginationService.builder();
		builder.title(Text.of(character.getName(), Color.YELLOW));
		builder.padding(Text.of("═", Color.GRAY));

		List<Text> content = new ArrayList<>();
		Set<CharacterClass> characterClasses = base.getCharacterClasses();
		for (CharacterClass cc : characterClasses) {
			Text t = Text.builder().append(Text.of(Utils.capitalizeFirst(cc.getName()), Color.GREEN))
					.append(Text.of(" - ", TextColors.GRAY))
					.append(Text.of(cc.getSkillPoints(), TextColors.BLUE))
					.append(Text.of(String.format("(%s)", cc.getUsedSkillPoints()), TextColors.GRAY))

					.toText();
			content.add(t);
		}
		content.add(Text.builder().append(Text.of("Attribute points: ", TextColors.GREEN))
				.append(Text.of(character.getCharacterBase().getAttributePoints(), TextColors.AQUA))
				.append(Text.of(String.format("(%s)", character.getCharacterBase().getUsedAttributePoints(), TextColors.GRAY))).toText());

		builder.contents(content);
		builder.sendTo(character.getPlayer());
	}


	@Override
	public void showClassInfo(IActiveCharacter character, ConfigClass cc) {
		Inventory i = createPlayerGroupView(cc);

		ItemStack of = GuiHelper.itemStack(ItemTypes.DIAMOND);
		of.offer(new InventoryCommandItemMenuData("character set class " + cc.getName()));
		of.offer(Keys.DISPLAY_NAME, TextHelper.parse(Localization.CONFIRM));
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 0))).offer(of);

		ItemStack tree = GuiHelper.itemStack(ItemTypes.SAPLING);
		tree.offer(Keys.DISPLAY_NAME, TextHelper.parse(Localization.SKILLTREE));
		tree.offer(new InventoryCommandItemMenuData("skilltree " + cc.getName()));
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(4,3))).offer(tree);

		character.getPlayer().openInventory(i);
	}

	@Override
	public void sendListOfCharacters(final IActiveCharacter player, CharacterBase currentlyCreated) {
		PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
		PaginationList.Builder builder = paginationService.builder();
		NtRpgPlugin.asyncExecutor.execute(() -> {
			DirectAccessDao build = IoC.get().build(DirectAccessDao.class);
			//language=HQL
			String query = "select new cz.neumimto.rpg.utils.model.CharacterListModel(" +
					"c.name,d.name,d.experiences) " +
					"from CharacterBase c left join c.characterClasses d " +
					"where c.uuid = :id order by c.updated desc";
			Map<String, Object> map = new HashMap<>();
			map.put("id", player.getPlayer().getUniqueId());
			List<CharacterListModel> list = build.findList(CharacterListModel.class, query, map);
			List<Text> content = new ArrayList<Text>();
			builder.linesPerPage(10);
			builder.padding(Text.builder("=").color(TextColors.DARK_GRAY).build());
			GroupService s = IoC.get().build(GroupService.class);
			String current = player.getName();

			list.forEach(a -> {
				Text.Builder b = Text.builder(" -")
						.color(TextColors.GRAY);
				if (!a.getCharacterName().equalsIgnoreCase(current)) {
					b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
							.append(Text.builder("SELECT").color(TextColors.GREEN).onClick(TextActions.runCommand("/character switch " + a.getCharacterName())).build())
							.append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
				} else {
					b.append(Text.builder(" [").color(TextColors.DARK_GRAY).build())
							.append(Text.builder("*").color(TextColors.RED).build())
							.append(Text.builder("] - ").color(TextColors.DARK_GRAY).build());
				}
				b.append(Text.builder(a.getCharacterName()).color(TextColors.GRAY).append(Text.of(" ")).build());
				ConfigClass cc = s.getNClass(a.getPrimaryClassName());
				int level = 0;
				int m = 0;
				if (cc != ConfigClass.Default) {
					b.append(Text.builder(a.getPrimaryClassName()).color(TextColors.AQUA).append(Text.of(" ")).build());
					level = s.getLevel(cc, a.getPrimaryClassExp());
					m = cc.getMaxLevel();

					b.append(Text.builder("Level: ").color(TextColors.DARK_GRAY).append(
							Text.builder(level + "").color(level == m ? TextColors.RED : TextColors.DARK_PURPLE).build()).build());
				}
				content.add(b.build());
			});
			builder.title(Text.of("Characters", TextColors.WHITE))
					.contents(content);
			builder.sendTo(player.getEntity());

		});
	}

	@Override
	public void sendListOfRunes(IActiveCharacter character) {
		PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
		PaginationList.Builder builder = paginationService.builder();

		List<Text> content = new ArrayList<>();
		List<Rune> r = new ArrayList<>(rwService.getRunes().values());
		for (Rune rune : r) {
			LiteralText.Builder b = Text.builder(rune.getName()).color(TextColors.GOLD);
			content.add(b.build());
		}
		builder.contents(content);
		builder.linesPerPage(10);
		builder.padding(Text.builder("=").color(TextColors.DARK_GRAY).build());
		builder.sendTo(character.getPlayer());


	}

	@Override
	public void showAvalaibleClasses(IActiveCharacter character) {
		displayCommonMenu(character, groupService.getClasses(), ConfigClass.Default, TextHelper.parse(Localization.CLASSES_MENU_TEXT));
	}

	@Override
	public void sendListOfRaces(IActiveCharacter character) {
		displayCommonMenu(character, groupService.getRaces(), Race.Default, TextHelper.parse(Localization.RACES_MENU_TEXT));
	}

	private void displayCommonMenu(IActiveCharacter character, Collection<? extends PlayerGroup> g, PlayerGroup default_, Text invHeader) {
		Inventory i = Inventory.builder()
				.of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryTitle.of(invHeader))
				.build(plugin);
		Player player = character.getPlayer();
		for (PlayerGroup cc : g) {
			if (cc == default_) {
				continue;
			}
			if (!cc.isShowsInMenu() && !player.hasPermission("ntrpg.admin")) {
				continue;
			}
			i.offer(createItemRepresentingGroup(cc));
		}
		player.openInventory(i);
	}

	private ItemStack createItemRepresentingGroup(PlayerGroup p) {
		ItemStack s = GuiHelper.itemStack(p.getItemType());
		s.offer(new MenuInventoryData(true));
		s.offer(Keys.DISPLAY_NAME, Text.of(p.getName(), TextColors.DARK_PURPLE));
		s.offer(Keys.ITEM_LORE, getItemLore(p.getDescription()));
		String l = "race ";
		if (p.getType() == EffectSourceType.CLASS) {
			l = "class ";
		}
		s.offer(new InventoryCommandItemMenuData(l + p.getName()));
		return s;
	}

	@Override
	public void displayGroupArmor(PlayerGroup g, Player target) {
		Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
		List<List<RPGItemType>> rows = new ArrayList<>(5);
		for (int ki = 0; ki <= 5; ki++) {
			rows.add(new ArrayList<>());
		}
		for (RPGItemType type : g.getAllowedArmor()) {
			if (ItemStackUtils.isHelmet(type.getItemType())) {
				rows.get(0).add(type);
			} else if (ItemStackUtils.isChestplate(type.getItemType())) {
				rows.get(1).add(type);
			} else if (ItemStackUtils.isLeggings(type.getItemType())) {
				rows.get(2).add(type);
			} else if (ItemStackUtils.isBoots(type.getItemType())) {
				rows.get(3).add(type);
			} else {
				rows.get(4).add(type);
			}
		}

		ItemStack of = GuiHelper.back(g);
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(of);

		int x = 2;
		int y = 0;
		for (List<RPGItemType> row : rows) {
			y = 0;
			for (RPGItemType type : row) {
				ItemStack armor = GuiHelper.itemStack(type.getItemType());
				if (type.getDisplayName() != null) {
					armor.offer(Keys.DISPLAY_NAME, Text.of(type.getDisplayName()));
				}
				armor.offer(new MenuInventoryData(true));
				i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(armor);
				y++;
			}
			x++;
		}
		target.openInventory(i);
	}

	@Override
	public void displayGroupWeapon(PlayerGroup g, Player target) {
		Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);


		ItemStack of = back(g);
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(of);

		TreeSet<ConfigRPGItemType> treeSet = new TreeSet<>();
		for (Map.Entry<ItemType, Set<ConfigRPGItemType>> entry : g.getWeapons().entrySet()) {
			treeSet.addAll(entry.getValue());
		}

		for (ConfigRPGItemType configRPGItemType : treeSet) {
			ItemStack q = GuiHelper.rpgItemTypeToItemStack(configRPGItemType);
			i.offer(q);
		}

		target.openInventory(i);
	}

	@Override
	public void sendRaceInfo(IActiveCharacter target, Race race) {
		Inventory i = createPlayerGroupView(race);
		if ((target.getRace() == null || target.getRace() == Race.Default) || PluginConfig.PLAYER_CAN_CHANGE_RACE) {
			ItemStack of = GuiHelper.itemStack(ItemTypes.DIAMOND);
			of.offer(new InventoryCommandItemMenuData("character set race " + race.getName()));
			of.offer(Keys.DISPLAY_NAME, Text.of(Localization.CONFIRM));
			i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8, 0))).offer(of);
		}
		target.getPlayer().openInventory(i);
	}

	@Override
	public void sendClassInfo(IActiveCharacter target, ConfigClass configClass) {
		Inventory i = createPlayerGroupView(configClass);
		target.getPlayer().openInventory(i);
	}

	@Override
	public void displayAttributes(Player player, PlayerGroup group) {
		Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(back(group));

		int x = 1;
		int y = 1;
		for (Map.Entry<ICharacterAttribute, Integer> a : group.getStartingAttributes().entrySet()) {
			ICharacterAttribute key = a.getKey();
			Integer value = a.getValue();
			i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(createAttributeItem(key, value));
			//somehow format them in square-like structure
			if (x == 7) {
				x = 1;
				y++;
			} else {
				x++;
			}
		}
		player.openInventory(i);
	}

	@Override
	public void displayRuneword(IActiveCharacter character, RuneWord rw, boolean linkToRWList) {
		Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
		String cmd = infoCommand.getAliases().get(0);
		if (linkToRWList) {
			if (character.getPlayer().hasPermission(CommandPermissions.SHOW_RUNEWORD_LIST)) {
				i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(back("runes", Localization.RUNE_LIST));
			}
		}

		List<ItemStack> commands = new ArrayList<>();
		if (!rw.getAllowedItems().isEmpty()) {
			ItemStack is = GuiHelper.itemStack(ItemTypes.IRON_PICKAXE);
			is.offer(Keys.DISPLAY_NAME, Text.of(Localization.RUNEWORD_ITEMS_MENU));
			is.offer(Keys.ITEM_LORE,
					Collections.singletonList(
							TextHelper.parse(Localization.RUNEWORD_ITEMS_MENU_TOOLTIP
									, Arg.arg("runeword", rw.getName()))
					)
			);
			is.offer(new InventoryCommandItemMenuData("runeword " + rw.getName() + " allowed-items"));
			commands.add(is);
		}

		if (!rw.getAllowedGroups().isEmpty()) {
			ItemStack is = GuiHelper.itemStack(ItemTypes.LEATHER_HELMET);
			is.offer(Keys.DISPLAY_NAME, Text.of(Localization.RUNEWORD_ALLOWED_GROUPS_MENU));
			is.offer(Keys.ITEM_LORE,
					Collections.singletonList(
							ItemStackUtils.stringToItemTooltip(Localization.RUNEWORD_ALLOWED_GROUPS_MENU_TOOLTIP
									.replaceAll("%1", rw.getName()))
					)
			);
			is.offer(Keys.HIDE_ATTRIBUTES, true);
			is.offer(new InventoryCommandItemMenuData("runeword " + rw.getName() + " allowed-groups"));
			commands.add(is);
		}

		if (!rw.getAllowedGroups().isEmpty()) {
			ItemStack is = GuiHelper.itemStack(ItemTypes.REDSTONE);
			is.offer(Keys.DISPLAY_NAME, Text.of(Localization.RUNEWORD_BLOCKED_GROUPS_MENU));
			is.offer(Keys.ITEM_LORE,
					Collections.singletonList(
							ItemStackUtils.stringToItemTooltip(Localization.RUNEWORD_BLOCKED_GROUPS_MENU_TOOLTIP
									.replaceAll("%1", rw.getName()))
					)
			);
			is.offer(new InventoryCommandItemMenuData("runeword " + rw.getName() + " blocked-groups"));
			commands.add(is);
		}

		for (int q = 0; q < commands.size(); q++) {
			i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(q + 2, 2))).offer(commands.get(q));
		}

		if (character.getPlayer().hasPermission(CommandPermissions.SWOW_RUNEWORD_COMBINATION)) {
			int x = 1;
			int y = 4;
			if (rw.getRunes().size() <= 7) {
				for (ItemUpgrade rune : rw.getRunes()) {
					/*ItemStack is = rwService.createRune(rune);
					is.offer(new MenuInventoryData(true));
					i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(is);
					x++;
					*/
				}
			} else {
				ItemStack is = ItemStack.of(rwService.getAllowedRuneItemTypes().get(0), rw.getRunes().size());
				is.offer(new MenuInventoryData(true));
				StringBuilder s = null;
				for (ItemUpgrade rune : rw.getRunes()) {
					s.append(rune.getName());
				}
				is.offer(Keys.DISPLAY_NAME, Text.of(s.toString(), TextColors.GOLD));
				i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(is);
			}
		}

		character.getPlayer().openInventory(i);
	}


	@Override
	public void displayRunewordBlockedGroups(IActiveCharacter character, RuneWord rw) {
		character.getPlayer().openInventory(displayGroupRequirements(character, rw, rw.getAllowedGroups()));
	}

	@Override
	public void displayRunewordRequiredGroups(IActiveCharacter character, RuneWord rw) {

	}

	@Override
	public void displayRunewordAllowedGroups(IActiveCharacter character, RuneWord rw) {

	}

	@Override
	public void displayRunewordAllowedItems(IActiveCharacter character, RuneWord rw) {
		Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(back("runeword " + rw.getName(), Localization.RUNEWORD_DETAILS_MENU));
		int x = 1;
		int y = 2;
		for (ItemType type : rw.getAllowedItems()) {
			i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(GuiHelper.itemStack(type));
			if (x == 7) {
				x = 1;
				y++;
			} else {
				x++;
			}
		}

	}

	private Inventory displayGroupRequirements(IActiveCharacter character, RuneWord rw, Set<PlayerGroup> groups) {
		Inventory i = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST).build(plugin);
		String cmd = infoCommand.getAliases().get(0);
		i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(0, 0))).offer(back("runeword " + rw.getName(), Localization.RUNEWORD_DETAILS_MENU));

		List<ItemStack> list = new ArrayList<>();
		for (PlayerGroup playerGroup : groups) {
			list.add(runewordRequirementsToItemStack(character, playerGroup));
		}
		int x = 1;
		int y = 2;
		for (ItemStack itemStack : list) {
			i.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(x, y))).offer(itemStack);
			if (x == 7) {
				x = 1;
				y++;
			} else {
				x++;
			}
		}
		return i;
	}

	private ItemStack runewordRequirementsToItemStack(IActiveCharacter character, PlayerGroup playerGroup) {
		ItemStack is = createItemRepresentingGroup(playerGroup);
		TextColor color = hasGroup(character, playerGroup);
		is.offer(Keys.DISPLAY_NAME, Text.of(color, playerGroup.getName()));
		return is;
	}

	private TextColor hasGroup(IActiveCharacter character, PlayerGroup playerGroup) {
		if (playerGroup.getType() == EffectSourceType.RACE) {
			return character.getRace() == playerGroup ? TextColors.GREEN : TextColors.RED;
		}
		if (playerGroup.getType() == EffectSourceType.CLASS) {
			return character.hasClass(playerGroup) ? TextColors.GREEN : TextColors.RED;
		}
		return null;
	}

	private ItemStack createAttributeItem(ICharacterAttribute key, Integer value) {
		ItemStack of = GuiHelper.itemStack(key.getItemRepresentation());
		of.offer(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_RED, key.getName()));
		List<Text> lore = new ArrayList<>();
		of.offer(new MenuInventoryData(true));
		lore.add(Text.of(Localization.INITIAL_VALUE + ": " + value, TextColors.WHITE));
		lore.addAll(getItemLore(key.getDescription()));
		of.offer(Keys.ITEM_LORE, lore);
		return of;
	}

	@Listener
	public void onOptionSelect(ClickInventoryEvent event, @First(typeFilter = Player.class) Player player) {
		//todo inventory.getPlugin

		Iterator<SlotTransaction> iterator = event.getTransactions().iterator();

		while (iterator.hasNext()) {
			SlotTransaction t = iterator.next();
			Optional<String> s = t.getOriginal().get(NKeys.COMMAND);
			if (s.isPresent()) {
				event.setCancelled(true);
				Sponge.getScheduler().createTaskBuilder()
						.delay(1L, TimeUnit.MILLISECONDS)
						.execute(() -> {

							Sponge.getCommandManager().process(player, s.get());
						})
						.submit(plugin);
				return;
			}

			if (t.getOriginal().get(NKeys.MENU_INVENTORY).isPresent()) {
				event.setCancelled(true);
				t.setCustom(ItemStack.empty());
			}

			if (t.getOriginal().get(NKeys.SKILLTREE_CONTROLLS).isPresent()) {
				SkillTreeControllsButton command = t.getOriginal().get(NKeys.SKILLTREE_CONTROLLS).get();
				IActiveCharacter character = characterService.getCharacter(player);
				SkillTreeViewModel viewModel = character.getLastTimeInvokedSkillTreeView();
				switch (command) {
					case NORTH:
						viewModel.getLocation().key-=1;
						Sponge.getScheduler().createTaskBuilder()
								.execute(() -> Gui.moveSkillTreeMenu(character))
								.submit(plugin);

						break;
					case SOUTH:
						viewModel.getLocation().key+=1;
						Sponge.getScheduler().createTaskBuilder()
								.execute(() -> Gui.moveSkillTreeMenu(character))
								.submit(plugin);
						break;
					case WEST:
						viewModel.getLocation().value+=1;
						Sponge.getScheduler().createTaskBuilder()
								.execute(() -> Gui.moveSkillTreeMenu(character))
								.submit(plugin);
						break;
					case EAST:
						viewModel.getLocation().value-=1;
						Sponge.getScheduler().createTaskBuilder()
								.execute(() -> Gui.moveSkillTreeMenu(character))
								.submit(plugin);
						break;
					case MODE:
						viewModel.setInteractiveMode(viewModel.getInteractiveMode().opposite());
						//just redraw
						Sponge.getScheduler().createTaskBuilder()
								.execute(() -> Gui.moveSkillTreeMenu(character))
								.submit(plugin);
						break;
					default:
						String node = t.getOriginal().get(NKeys.SKILLTREE_NODE).get();
						if (viewModel.getInteractiveMode() == SkillTreeViewModel.InteractiveMode.FAST) {

							ISkill iSkill = skillService.getSkill(node);
							SkillTree tree = character.getPrimaryClass().getConfigClass().getSkillTree();
							if (character.getSkill(node) == null) {
								Pair<SkillTreeActionResult, SkillTreeActionResult.Data> data = characterService.characterLearnskill(character, iSkill, tree);
								player.sendMessage(data.value.bind(data.key.message));
							} else {
								Pair<SkillTreeActionResult, SkillTreeActionResult.Data> data = characterService.upgradeSkill(character, iSkill);
								player.sendMessage(data.value.bind(data.key.message));
							}
							//redraw
							Sponge.getScheduler().createTaskBuilder()
									.execute(() -> Gui.moveSkillTreeMenu(character))
									.submit(plugin);
						} else {
							SkillTree tree = character.getPrimaryClass().getConfigClass().getSkillTree();
							event.setCancelled(true);
							Sponge.getScheduler().createTaskBuilder()
									.execute(() -> Gui.displaySkillDetailsInventoryMenu(character, tree, node))
									.submit(plugin);

						}

				}
			}
		}
	}




	@Override
	public void displayHealth(IActiveCharacter character) {
		double value = character.getHealth().getValue();
		double maxValue = character.getHealth().getMaxValue();
		//todo implement
		//double reservedAmount = character.getHealth().getReservedAmount();

		LiteralText a = Text.builder(Localization.HEALTH).color(TextColors.GOLD)
				.append(Text.builder(value + "").color(TextColors.GREEN).build())
				.append(Text.builder("/").color(TextColors.WHITE).build())
				//		.append(Text.builder(String.valueOf(maxValue - reservedAmount)).color(TextColors.RED).build())
				.append(Text.builder(" (" + maxValue + ") ").color(TextColors.GRAY).build()).build();
		character.getPlayer().sendMessage(a);
	}

	@Override
	public void displayMana(IActiveCharacter character) {
		IEffectContainer<Object, ManaBarNotifier> barExpNotifier = character.getEffect(ManaBarNotifier.name);
		ManaBarNotifier effect = (ManaBarNotifier) barExpNotifier;
		if (effect == null) {
			effect = new ManaBarNotifier(character);
			effectService.addEffect(effect, character, InternalEffectSourceProvider.INSTANCE);
		}
		effect.notifyManaChange();

	}

	@Override
	public void sendCannotUseItemNotification(IActiveCharacter character, ItemStack is, CannotUseItemReson reason) {
		if (reason == CannotUseItemReson.CONFIG) {
			character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, Localization.CANNOT_USE_ITEM_CONFIGURATION_REASON));
		} else if (reason == CannotUseItemReson.LEVEL) {
			character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, Localization.CANNOT_USE_ITEM_LEVEL_REASON));
		} else if (reason == CannotUseItemReson.LORE) {
			character.getPlayer().sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.RED, Localization.CANNOT_USE_ITEM_LORE_REASON));
		}
	}

	@Override
	public void openSkillTreeMenu(IActiveCharacter player) {
		SkillTree skillTree = player.getLastTimeInvokedSkillTreeView().getSkillTree();
		if (player.getSkillTreeViewLocation().get(skillTree.getId()) == null){
			SkillTreeViewModel skillTreeViewModel = new SkillTreeViewModel();
			for (SkillTreeViewModel treeViewModel : player.getSkillTreeViewLocation().values()) {
				treeViewModel.setCurrent(false);
			}
			player.getSkillTreeViewLocation().put(skillTree.getId(), skillTreeViewModel);
			skillTreeViewModel.setSkillTree(skillTree);
		}
		Inventory skillTreeInventoryViewTemplate = GuiHelper.createSkillTreeInventoryViewTemplate(player, skillTree);
		createSkillTreeView(player, skillTreeInventoryViewTemplate);
		player.getPlayer().openInventory(skillTreeInventoryViewTemplate);

	}

	@Override
	public void moveSkillTreeMenu(IActiveCharacter character) {
		Optional<Container> openInventory = character.getPlayer().getOpenInventory();
		if (openInventory.isPresent()) {
			createSkillTreeView(character, openInventory.get().query(GridInventory.class).first());
		}
	}

	@Override
	public void displaySkillDetailsInventoryMenu(IActiveCharacter character, SkillTree tree, String command) {

		Inventory skillDetailInventoryView = GuiHelper.createSkillDetailInventoryView(character, tree.getId(), tree.getSkills().get(command));
		character.getPlayer().openInventory(skillDetailInventoryView);
	}

	@Override
	public void displayInitialProperties(PlayerGroup g, Player p) {
		ItemStack back = GuiHelper.back(g);

	}

	private void createSkillTreeView(IActiveCharacter character, Inventory skillTreeInventoryViewTemplate) {

		SkillTreeViewModel skillTreeViewModel = character.getLastTimeInvokedSkillTreeView();
		SkillTree skillTree = skillTreeViewModel.getSkillTree();
		short[][] skillTreeMap = skillTreeViewModel.getSkillTree().getSkillTreeMap();
		int y = skillTree.getCenter().value + skillTreeViewModel.getLocation().value; //y
		int x = skillTree.getCenter().key + skillTreeViewModel.getLocation().key; //x

		if (skillTreeMap == null) {
			throw new IllegalStateException("No AsciiMap defined for skilltree: " + skillTree.getId());
		}

		int columns = skillTreeMap[0].length;
		int rows = skillTreeMap.length;

		SkillTreeViewModel.InteractiveMode interactiveMode = skillTreeViewModel.getInteractiveMode();
		ItemStack md = GuiHelper.interactiveModeToitemStack(character, interactiveMode);
		skillTreeInventoryViewTemplate.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8,1))).clear();
		skillTreeInventoryViewTemplate.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(8,1))).offer(md);


		for (int k = -3; k <= 3; k++) { //x
			for (int l = -3; l <= 3; l++) { //y
				Inventory query = skillTreeInventoryViewTemplate
						.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotPos.of(l + 3, k + 3)));
				query.clear();
				if (x + k >= 0 && x + k < rows) {
					if (l + y >= 0 && l + y < columns) {

						short id = skillTreeMap[x+k][l+y];
						ItemStack itemStack = null;
						if (id > 0) {
							SkillTreeInterfaceModel guiModelById = skillService.getGuiModelById(id);
							if (guiModelById != null) {
								itemStack = guiModelById.toItemStack();
							} else {
								SkillData skillById = skillTree.getSkillById(id);

								if (skillById == null) {
									itemStack = GuiHelper.itemStack(ItemTypes.BARRIER);
									itemStack.offer(Keys.DISPLAY_NAME, Text.of("UNKNOWN SKILL ID: " + id));
									itemStack.offer(new MenuInventoryData(true));
								} else {
									itemStack = GuiHelper.skillToItemStack(character, skillById);
									itemStack.offer(new SkillTreeInventoryViewControllsData(SkillTreeControllsButton.NODE));
									itemStack.offer(new MenuInventoryData(true));
									itemStack.offer(new SkillTreeNode(skillById.getSkill().getName()));
								}
							}
						}
						if (itemStack == null) {
							itemStack = ItemStack.of(ItemTypes.STAINED_GLASS_PANE, 1);
							itemStack.offer(Keys.DISPLAY_NAME, Text.EMPTY);
							itemStack.offer(Keys.DYE_COLOR, DyeColors.GRAY);
							itemStack.offer(Keys.HIDE_MISCELLANEOUS, true);
							itemStack.offer(new MenuInventoryData(true));
						}
						query.offer(itemStack);
					} else {
					//	SlotPos slotPos = new SlotPos(l + 3, k + 3);
						query.offer(GuiHelper.createSkillTreeInventoryMenuBoundary());
					}
				} else {
					query.offer(GuiHelper.createSkillTreeInventoryMenuBoundary());
				}
			}
		}
	}
}
