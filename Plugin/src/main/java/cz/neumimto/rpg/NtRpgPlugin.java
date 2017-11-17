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

package cz.neumimto.rpg;

import com.google.inject.Inject;
import cz.neumimto.configuration.ConfigMapper;
import cz.neumimto.core.FindPersistenceContextEvent;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.commands.*;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.configuration.Settings;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.data.CustomItemData;
import cz.neumimto.rpg.inventory.data.InventoryCommandItemMenuData;
import cz.neumimto.rpg.inventory.data.MenuInventoryData;
import cz.neumimto.rpg.inventory.data.SkillTreeInventoryViewControllsData;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import cz.neumimto.rpg.listeners.DebugListener;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.persistance.model.CharacterSkill;
import cz.neumimto.rpg.players.*;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.FileUtils;
import cz.neumimto.rpg.utils.SkillTreeActionResult;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by NeumimTo on 29.4.2015.
 */
@Plugin(id = "nt-rpg", version = Version.VERSION, name = "NT-Rpg", dependencies = {
		@Dependency(id = "nt-core", version = "1.9", optional = false)
})
public class NtRpgPlugin {
	public static String workingDir;
	public static File pluginjar;
	public static GlobalScope GlobalScope;
	public static SpongeExecutorService asyncExecutor;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path config;


	@Inject
	public Logger logger;


	@Listener
	public void preinit(GamePreInitializationEvent e) {

		DataRegistration.<InventoryCommandItemMenuData, InventoryCommandItemMenuData.Immutable>builder()
				.dataClass(InventoryCommandItemMenuData.class)
				.immutableClass(InventoryCommandItemMenuData.Immutable.class)
				.builder(new InventoryCommandItemMenuData.Builder())
				.manipulatorId("ntrpg-custominventory")
				.dataName("CustomInventory")
				.buildAndRegister(Sponge.getPluginManager().getPlugin("nt-rpg").get());

		DataRegistration.<MenuInventoryData, MenuInventoryData.Immutable>builder()
				.dataClass(MenuInventoryData.class)
				.immutableClass(MenuInventoryData.Immutable.class)
				.builder(new MenuInventoryData.Builder())
				.manipulatorId("ntrpg-menuinventory")
				.dataName("MenuItem")
				.buildAndRegister(Sponge.getPluginManager().getPlugin("nt-rpg").get());


		DataRegistration.<CustomItemData, CustomItemData.Immutable>builder()
				.dataClass(CustomItemData.class)
				.immutableClass(CustomItemData.Immutable.class)
				.builder(new CustomItemData.Builder())
				.manipulatorId("ntrpg-customitemdata")
				.dataName("CustomItemData")
				.buildAndRegister(Sponge.getPluginManager().getPlugin("nt-rpg").get());


		DataRegistration.<CustomItemData, CustomItemData.Immutable>builder()
				.dataClass(SkillTreeInventoryViewControllsData.class)
				.immutableClass(SkillTreeInventoryViewControllsData.Immutable.class)
				.builder(new SkillTreeInventoryViewControllsData.Builder())
				.manipulatorId("ntrpg-stivcd")
				.dataName("SkillTreeInventoryViewControllsData")
				.buildAndRegister(Sponge.getPluginManager().getPlugin("nt-rpg").get());

	}

	@Listener
	public void registerEntities(FindPersistenceContextEvent event) {
		event.getClasses().add(CharacterBase.class);
		event.getClasses().add(BaseCharacterAttribute.class);
		event.getClasses().add(CharacterSkill.class);
		event.getClasses().add(CharacterClass.class);

	}

	@Listener
	public void onPluginLoad(GamePostInitializationEvent event) {
		long start = System.nanoTime();
		IoC ioc = IoC.get();
		asyncExecutor = Sponge.getGame().getScheduler().createAsyncExecutor(NtRpgPlugin.this);
		ioc.registerInterfaceImplementation(Logger.class, logger);
		Game game = Sponge.getGame();
		Optional<PluginContainer> gui = game.getPluginManager().getPlugin("MinecraftGUIServer");
		if (gui.isPresent()) {
			//ioc.registerInterfaceImplementation(MinecraftGuiService.class, game.getServiceManager().provide(MinecraftGuiService.class).get());
		} else {
			Settings.ENABLED_GUI = false;
		}
		ioc.registerDependency(this);
		ioc.registerInterfaceImplementation(CauseStackManager.class, Sponge.getCauseStackManager());
		try {
			workingDir = config.toString();
			URL url = FileUtils.getPluginUrl();
			pluginjar = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		Path path = Paths.get(workingDir);
		ConfigMapper.init("NtRpg", path);
		ioc.registerDependency(ConfigMapper.get("NtRpg"));
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ioc.get(IoC.class, ioc);
		ResourceLoader rl = ioc.build(ResourceLoader.class);
		rl.loadJarFile(pluginjar, true);
		GlobalScope = ioc.build(GlobalScope.class);
		rl.loadExternalJars();
		ioc.postProcess();
		if (PluginConfig.DEBUG) {
			Sponge.getEventManager().registerListeners(this, ioc.build(DebugListener.class));
		}
		registerCommands();
		IoC.get().build(PropertyService.class).loadMaximalServerPropertyValues();
		double elapsedTime = (System.nanoTime() - start) / 1000000000.0;
		logger.info("NtRpg plugin successfully loaded in " + elapsedTime + " seconds");
	}

	public void registerCommands() {
		registerAdminCommands();
		registerCharacterCommands();
		registerSkillCommands();
	}



	public void registerAdminCommands() {
		// ===========================================================
		// ==================         SKILLS        ==================
		// ===========================================================
		CommandSpec executeSkill = CommandSpec.builder()
				.description(TextSerializers
						.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_ADMIN_EXEC_SKILL_DESC))
				.arguments(
						new AnySkillCommandElement(TextHelper.parse("skill")),
						GenericArguments.flags().valueFlag(GenericArguments
								.integer(TextHelper.parse("level")), "l")
								.buildWith(GenericArguments.none())
				)
				.executor((src, args) -> {
					ISkill skill = args.<ISkill>getOne("skill").get();
					SkillSettings defaultSkillSettings = skill.getDefaultSkillSettings();
					Player player = (Player) src;
					IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
					if (character.isStub())
						throw new RuntimeException("Character is required even for an admin.");

					int level = 1;
					Optional<Integer> optional = args.getOne("level");
					if (optional.isPresent()) {
						level = optional.get();
					}
					if (skill instanceof ActiveSkill) {
						Long l = System.nanoTime();
						ExtendedSkillInfo extendedSkillInfo = new ExtendedSkillInfo();
						extendedSkillInfo.setLevel(level);
						SkillData skillData = new SkillData(skill.getName());
						skillData.setSkillSettings(defaultSkillSettings);
						extendedSkillInfo.setSkillData(skillData);
						extendedSkillInfo.setSkill(skill);
						ActiveSkill askill = (ActiveSkill) skill;
						askill.cast(character, extendedSkillInfo, null);
						Long e = System.nanoTime();
						character.sendMessage("Exec Time: " + TimeUnit.MILLISECONDS.convert(e - l, TimeUnit.NANOSECONDS));
					}
					return CommandResult.success();
				})
				.build();

		// ===========================================================
		// ==================        ENCHANTS       ==================
		// ===========================================================

		CommandSpec enchantAdd = CommandSpec.builder()
				.description(TextSerializers
						.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_ADMIN_ENCHANT_ADD))
				.arguments(
						new GlobalEffectCommandElement(TextHelper.parse("effect")),
						GenericArguments.remainingJoinedStrings(TextHelper.parse("params"))
				)
				.executor((src, args) -> {
					IGlobalEffect effect = args.<IGlobalEffect>getOne("effect").get();
					Player player = (Player) src;
					if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
						ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();
						CustomItemData itemData = NtRpgPlugin.GlobalScope.inventorySerivce.getItemData(itemStack);
						Map<String, String> map = new HashMap<>();
						map.putAll(itemData.getEnchantements());

						String message = args.<String>getOne("args").orElse("");
						map.put(effect.getName(), message);

						itemStack = NtRpgPlugin.GlobalScope.inventorySerivce.setEnchantments(map, itemStack);
						player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
						player.sendMessage(TextHelper.parse("Enchantment " + effect.getName() + " added"));
					} else {
						player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Localization.NO_ITEM_IN_HAND));
					}
					return CommandResult.success();
				})
				.build();


		CommandSpec enchant = CommandSpec.builder()
				.description(
						TextSerializers
								.FORMATTING_CODE
								.deserialize(CommandLocalization.COMMAND_ADMIN_ENCHANT))
				.arguments(
						new GlobalEffectCommandElement(TextHelper.parse("effect")),
						GenericArguments.remainingJoinedStrings(TextHelper.parse("args")))
				.child(enchantAdd, "add", "e")
				.build();

		// ===========================================================
		// ==================         SOCKET        ==================
		// ===========================================================

		CommandSpec socket = CommandSpec.builder()
				.description(
						TextSerializers
								.FORMATTING_CODE
								.deserialize(CommandLocalization.COMMAND_ADMIN_SOCKET))
				.arguments(
						GenericArguments.onlyOne(GenericArguments.integer(TextHelper.parse("count")))
				)
				.executor((src, args) -> {
					Player player = (Player) src;
					Integer count = args.<Integer>getOne("count").orElse(1);
					Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
					if (itemInHand.isPresent()) {
						ItemStack itemStack = NtRpgPlugin.GlobalScope.runewordService.createSockets(itemInHand.get(), count);
						player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
					}
					return CommandResult.success();
				})
				.build();

		// ===========================================================
		// ==================          RUNE         ==================
		// ===========================================================

		CommandSpec rune = CommandSpec.builder()
				.description(
						TextSerializers
								.FORMATTING_CODE
								.deserialize(CommandLocalization.COMMAND_ADMIN_RUNE))
				.arguments(
						new RuneCommandElement(TextHelper.parse("rune"))
				)
				.executor((src, args) -> {
					Rune runee = args.<Rune>getOne("rune").get();
					Player player = (Player) src;
					ItemStack is = NtRpgPlugin.GlobalScope.runewordService.toItemStack(runee);
					player.getInventory().offer(is);
					return CommandResult.success();
				})
				.build();

		// ===========================================================
		// ==================           RW          ==================
		// ===========================================================

		CommandSpec runeword = CommandSpec.builder()
				.description(
						TextSerializers
								.FORMATTING_CODE
								.deserialize(CommandLocalization.COMMAND_ADMIN_RUNEWORD))
				.arguments(
						new RuneCommandElement(TextHelper.parse("rw"))
				)
				.executor((src, args) -> {
					RuneWord r = args.<RuneWord>getOne("rw").get();
					Player p = (Player) src;
					Optional<ItemStack> itemInHand = p.getItemInHand(HandTypes.MAIN_HAND);
					if (itemInHand.isPresent()) {
						ItemStack itemStack = itemInHand.get();
						ItemStack itemStack1 = NtRpgPlugin.GlobalScope.runewordService.reBuildRuneword(itemStack, r);
						p.setItemInHand(HandTypes.MAIN_HAND, itemStack1);
					}
					return CommandResult.success();
				})
				.build();

		// ===========================================================
		// ==================          EXP          ==================
		// ===========================================================

		CommandSpec expadd = CommandSpec.builder()
				.description(
						TextSerializers
								.FORMATTING_CODE
								.deserialize(CommandLocalization.COMMAND_ADMIN_EXP_ADD))
				.arguments(
						GenericArguments.onlyOne(GenericArguments.player(TextHelper.parse("player"))),
						GenericArguments.remainingJoinedStrings(TextHelper.parse("data"))
				)
				.executor((src, args) -> {
					Player player = args.<Player>getOne("player").get();
					String data = args.<String>getOne("data").get();
					IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
					Set<ExtendedNClass> classes = character.getClasses();
					String[] a = data.split(" ");
					for (ExtendedNClass aClass : classes) {
						if (aClass.getConfigClass().getName().equalsIgnoreCase(a[0])) {
							NtRpgPlugin.GlobalScope.characterService.addExperiences(character, Double.valueOf(a[1]), aClass, false);
						}
					}
					return CommandResult.success();
				})
				.build();

		CommandSpec exp = CommandSpec.builder()
				.child(expadd, "add")
				.build();



		// ===========================================================
		// ==================          ROOT         ==================
		// ===========================================================

		CommandSpec reload = CommandSpec.builder()
				.description(TextSerializers
						.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_ADMIN_RELOAD))
				.arguments(GenericArguments.remainingJoinedStrings(TextHelper.parse("args")))
				.executor((src, args) -> {
					String[] a = args.<String>getOne("args").get().split(" ");
					if (a.length == 1) {
						src.sendMessage(TextHelper.parse("js[s/a/g] skilltree [r,a]"));
						return CommandResult.empty();
					}
					if (a[1].equalsIgnoreCase("js")) {
						if (!PluginConfig.DEBUG) {
							src.sendMessage(TextHelper.parse("Reloading is allowed only in debug mode"));
							return CommandResult.success();
						}
						JSLoader jsLoader = IoC.get().build(JSLoader.class);
						jsLoader.initEngine();

						int i = 1;
						String q = null;
						while (i < a.length) {
							q = a[i];
							if (q.equalsIgnoreCase("skills") || q.equalsIgnoreCase("s")) {
								jsLoader.reloadSkills();
							}
							if (q.equalsIgnoreCase("attributes") || q.equalsIgnoreCase("a")) {
								jsLoader.reloadAttributes();
							}
							if (q.equalsIgnoreCase("globaleffects") || q.equalsIgnoreCase("g")) {
								jsLoader.reloadGlobalEffects();
							}
							i++;
						}
					} else if (a[1].equalsIgnoreCase("skilltree")) {
						IoC.get().build(SkillService.class).reloadSkillTrees();
					}
					return CommandResult.success();
				})
				.build();

		CommandSpec adminRoot = CommandSpec
				.builder()
				.description(TextSerializers
						.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_ADMIN_DESC))
				.permission("ntrpg.admin")
				.child(executeSkill,"skill", "s")
				.child(enchant, "enchant", "e")
				.child(socket, "socket", "sk")
				.child(rune, "rune", "r")
				.child(runeword, "runeword", "rw")
				.child(exp, "experiences", "exp")
				.child(reload, "reload")
				.build();

		Sponge.getCommandManager().register(this, adminRoot, "nadmin", "na");
	}



	public void registerCharacterCommands() {


		// ===========================================================
		// ==============           CHAR CREATE         ==============
		// ===========================================================

		CommandSpec createCharacter = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CREATE_DESCRIPTION))
				.arguments(GenericArguments.remainingJoinedStrings(TextHelper.parse("name")))
				.permission("ntrpg.player.character.create")
				.executor((src, args) -> {
					String a = args.<String>getOne("name").get();
					CompletableFuture.runAsync(() -> {
						Player player = (Player) src;
						CharacterService characterService = IoC.get().build(CharacterService.class);
						int i = characterService.canCreateNewCharacter(player.getUniqueId(), a);
						if (i == 1) {
							src.sendMessage(TextHelper.parse(Localization.REACHED_CHARACTER_LIMIT));
						} else if (i == 2) {
							src.sendMessage(TextHelper.parse(Localization.CHARACTER_EXISTS));
						} else if (i == 0) {
							CharacterBase characterBase = new CharacterBase();
							characterBase.setName(a);
							characterBase.setRace(Race.Default.getName());
							characterBase.setPrimaryClass(ConfigClass.Default.getName());
							CharacterClass characterClass = new CharacterClass();
							characterClass.setName(ConfigClass.Default.getName());
							characterClass.setExperiences(0D);
							characterClass.setCharacterBase(characterBase);
							characterBase.setAttributePoints(PluginConfig.ATTRIBUTEPOINTS_ON_START);
							characterBase.getCharacterClasses().add(characterClass);
							characterBase.setUuid(player.getUniqueId());
							characterBase.setAttributePoints(PluginConfig.ATTRIBUTEPOINTS_ON_START);
							characterService.createAndUpdate(characterBase);
							src.sendMessage(TextHelper.parse(CommandLocalization.CHARACTER_CREATED.replaceAll("%1", characterBase.getName())));
							Gui.sendListOfCharacters(characterService.getCharacter(player.getUniqueId()), characterBase);
						}
					}, asyncExecutor);
					return CommandResult.success();
				})
				.build();

		CommandSpec setclass = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.arguments(new PlayerClassCommandElement(TextHelper.parse("class")))
				.permission("ntrpg.player.set.class")
				.executor((src, args) -> {
					ConfigClass configClass = args.<ConfigClass>getOne("class").get();
					if (configClass == ConfigClass.Default) {
						src.sendMessage(TextHelper.parse(Localization.NON_EXISTING_GROUP));
						return CommandResult.empty();
					}

					if (!src.hasPermission("ntrpg.classes."+configClass.getName().toLowerCase())) {
						src.sendMessage(TextHelper.parse(Localization.NO_PERMISSIONS));
						return CommandResult.empty();
					}
					int i = 0;
					/*
					if (args.length == 3) {
						i = Integer.parseInt(args[2]) - 1;
					}
					if (i < 0) {
						i = 0;
					}
					*/
					Player player = (Player) src;
					IActiveCharacter character = GlobalScope.characterService.getCharacter(player.getUniqueId());
					if (character.isStub()) {
						player.sendMessage(TextHelper.parse(Localization.CHARACTER_IS_REQUIRED));
						return CommandResult.empty();
					}
					if (character.getClasses().contains(ExtendedNClass.Default)) {
						character.getClasses().remove(ExtendedNClass.Default);
					}
					GlobalScope.characterService.updatePlayerGroups(character, configClass, i, null, null);
					player.sendMessage(TextHelper.parse(Localization.PLAYER_CHOOSED_CLASS.replaceAll("%1", configClass.getName())));
					return CommandResult.success();
				})
				.build();

		CommandSpec setrace = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.arguments(new RaceCommandElement(TextHelper.parse("race")))
				.permission("ntrpg.player.set.race")
				.executor((src, args) -> {
					IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
					if (character.isStub()) {
						character.getPlayer().sendMessage(TextHelper.parse(Localization.CHARACTER_IS_REQUIRED));
						return CommandResult.empty();
					}

					args.<Race>getOne(TextHelper.parse("race")).ifPresent(r -> {
						if (r == Race.Default) {
							character.getPlayer().sendMessage(TextHelper.parse(Localization.NON_EXISTING_GROUP));
							return;
						}

						if (!src.hasPermission("ntrpg.races" + r.getName().toLowerCase())) {
							src.sendMessage(TextHelper.parse(Localization.NO_PERMISSIONS));
							return;
						}

						if (character.getRace() == Race.Default ||
								(character.getRace() != Race.Default && PluginConfig.PLAYER_CAN_CHANGE_RACE)) {
							if (PluginConfig.PLAYER_CAN_CHANGE_RACE) {
								GlobalScope.characterService.updatePlayerGroups(character, null, 0, r, null);
								src.sendMessage(TextHelper.parse(Localization.PLAYER_CHOOSED_RACE, r.getName()));
								return ;
							}
							src.sendMessage(TextHelper.parse(Localization.PLAYER_CANT_CHANGE_RACE));
						}
					});
					return CommandResult.empty();
				})
				.build();

		CommandSpec cset = CommandSpec.builder()
				.child(setclass, "class", "c")
				.child(setrace, "race", "r")
				.build();

		CommandSpec learn = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_SKILL_LEARN))
				.arguments(new UnlearnedSkillCommandElement(TextHelper.parse("skill")))
				.permission("ntrpg.player.skills")
				.executor((src, args) -> {
					args.<ISkill>getOne(Text.of("skill")).ifPresent( iSkill -> {
						Player player = (Player) src;
						IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
						Pair<SkillTreeActionResult, SkillTreeActionResult.Data> data
								= GlobalScope.characterService.characterLearnskill(character, iSkill, character.getPrimaryClass().getConfigClass().getSkillTree());
						player.sendMessage(Text.of(data.value.bind(data.key.message)));
					});
					return CommandResult.empty();
				})
				.build();

		CommandSpec upgrade = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_SKILL_UPGRADE))
				.arguments(new LearnedSkillCommandElement(TextHelper.parse("skill")))
				.permission("ntrpg.player.skills")
				.executor((src, args) -> {
					args.<ISkill>getOne("skill").ifPresent(iSkill -> {
						Player player = (Player) src;
						IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
						Pair<SkillTreeActionResult, SkillTreeActionResult.Data> data = GlobalScope.characterService.upgradeSkill(character, iSkill);
						player.sendMessage(Text.of(data.value.bind(data.key.message)));
					});
					return CommandResult.success();
				})
				.build();

		CommandSpec refund = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_SKILL_REFUND))
				.arguments(new LearnedSkillCommandElement(TextHelper.parse("skill")))
				.permission("ntrpg.player.skills.refund")
				.executor((src, args) -> {
					args.<ISkill>getOne("skill").ifPresent(iSkill -> {
						Player player = (Player) src;
						IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
						int i = GlobalScope.characterService.refundSkill(character, iSkill, character.getPrimaryClass().getConfigClass());
					});
					return CommandResult.success();
				})
				.build();

		CommandSpec cskill = CommandSpec.builder()
				.child(learn, "learn", "l")
				.child(upgrade, "upgrade", "u")
				.child(refund, "refund", "r")
				.build();

		CommandSpec cattribute = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_ATTRIBUTE))
				.arguments(new CharacterAttributeCommandElement(Text.of("attribute")),
						GenericArguments.integer(Text.of("amount")))
				.executor((src, args) -> {
					args.<ICharacterAttribute>getOne(Text.of("attribute")).ifPresent(iCharacterAttribute -> {
						Integer i = args.<Integer>getOne("amount").orElse(1);
						IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
						GlobalScope.characterService.addAttribute(character, iCharacterAttribute, i);
						GlobalScope.characterService.putInSaveQueue(character.getCharacterBase());
					});
					return CommandResult.empty();
				})
				.build();

		CommandSpec cswitch = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
				.executor((src, args) -> {
					args.<String>getOne("name").ifPresent(s -> {
						Player player = (Player) src;
						IActiveCharacter current = GlobalScope.characterService.getCharacter(player);
						if (current.getName().equalsIgnoreCase(s)) {
							player.sendMessage(Text.of(Localization.ALREADY_CUURENT_CHARACTER));
							return;
						}
						asyncExecutor.schedule(() -> {
							List<CharacterBase> playersCharacters = GlobalScope.characterService.getPlayersCharacters(player.getUniqueId());
							boolean b = false;
							for (CharacterBase playersCharacter : playersCharacters) {
								if (playersCharacter.getName().equalsIgnoreCase(s)) {
									ActiveCharacter character = GlobalScope.characterService.buildActiveCharacterAsynchronously(player, playersCharacter);
									Sponge.getScheduler().createTaskBuilder().name("SetCharacterCallback" + player.getUniqueId())
											.execute(() -> GlobalScope.characterService.setActiveCharacter(player.getUniqueId(), character))
											.submit(NtRpgPlugin.this);
									b = true;
								}
							}
							if (!b)
								player.sendMessage(Text.of(Localization.NON_EXISTING_CHARACTER));
						},0,TimeUnit.SECONDS);
					});
					return CommandResult.success();
				})
				.build();

		CommandSpec characterRoot = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.child(createCharacter, "create", "c")
				.child(cset, "set", "s")
				.child(cskill, "skill", "s","sk")
				.child(cattribute, "attribute", "attr", "a")
				.child(cswitch, "switch")
				.build();

		Sponge.getCommandManager().register(this, characterRoot, "character", "char", "nc");

		// ===========================================================
		// =================          SKILLS         =================
		// ===========================================================

		CommandSpec skillexecute = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_HP_DESC))
				.arguments(new LearnedSkillCommandElement(TextHelper.parse("skill")))
				.executor((src, args) -> {
					IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
					args.<ISkill>getOne(TextHelper.parse("skill")).ifPresent(iSkill -> {
						ExtendedSkillInfo info = character.getSkillInfo(iSkill.getName());
						if (info == ExtendedSkillInfo.Empty || info == null) {
							src.sendMessage(TextHelper.parse(Localization.CHARACTER_DOES_NOT_HAVE_SKILL));
						}
						SkillResult sk = GlobalScope.skillService.executeSkill(character, info);
						switch (sk) {
							case ON_COOLDOWN:
								Gui.sendMessage(character, Localization.ON_COOLDOWN);
								break;
							case NO_MANA:
								Gui.sendMessage(character, Localization.NO_MANA);
								break;
							case NO_HP:
								Gui.sendMessage(character, Localization.NO_HP);
								break;
							case CASTER_SILENCED:
								Gui.sendMessage(character, Localization.PLAYER_IS_SILENCED);
								break;
							case NO_TARGET:
								Gui.sendMessage(character, Localization.NO_TARGET);
						}
					});
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, skillexecute, "skill", "skl", "ns");

		// ===========================================================
		// ==============              MP HP            ==============
		// ===========================================================


		CommandSpec hp = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_HP_DESC))
				.executor((src, args) -> {

					return CommandResult.success();
				})
				.build();

		CommandSpec mp = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_MP_DESC))
				.executor((src, args) -> {
					final Player player = (Player) src;
					IActiveCharacter character = GlobalScope.characterService.getCharacter(player);
					Gui.displayMana(character);
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, hp, "health", "mp");
		Sponge.getCommandManager().register(this, mp, "mana", "hp");

		// ===========================================================
		// =================          PARTY          =================
		// ===========================================================
		CommandSpec createparty = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.party.create")
				.executor((src, args) -> {
					IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
					if (character.isStub()) {
						Gui.sendMessage(character, Localization.CHARACTER_IS_REQUIRED);
						return CommandResult.success();
					}
					if (character.hasParty()) {
						Gui.sendMessage(character, Localization.ALREADY_IN_PARTY);
						return CommandResult.success();
					}
					Party party = new Party(character);
					character.setParty(party);
					Gui.sendMessage(character, Localization.PARTY_CREATED);
					return CommandResult.success();
				})
				.build();

		CommandSpec kick = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.party.create")
				.arguments(new PartyMemberCommandElement(TextHelper.parse("player")))
				.executor((src, args) -> {
					args.<IActiveCharacter>getOne(TextHelper.parse("player")).ifPresent(o -> {

						GlobalScope.partyService.kickCharacterFromParty(
								GlobalScope.characterService.getCharacter((Player) src).getParty(), o);
					});
					return CommandResult.success();
				})
				.build();


		CommandSpec invite = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.party.create")
				.arguments(GenericArguments.player(TextHelper.parse("player")))
				.executor((src, args) -> {
					args.<Player>getOne(TextHelper.parse("player")).ifPresent(o -> {
						GlobalScope.partyService.sendPartyInvite(
								GlobalScope.characterService.getCharacter((Player) src).getParty(),
								GlobalScope.characterService.getCharacter(o));
					});
					return CommandResult.success();
				})
				.build();



		CommandSpec accept = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.party.create")
				.executor((src, args) -> {
					IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
					if (character.getPendingPartyInvite() != null) {
						GlobalScope.partyService.addToParty(character.getPendingPartyInvite(), character);
					}
					return CommandResult.success();
				})
				.build();


		CommandSpec partyRoot = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.child(createparty, "create", "c")
				.child(kick, "kick", "k")
				.child(invite, "invite", "i")
				.child(accept, "accept", "a")
				.build();


		Sponge.getCommandManager().register(this, partyRoot, "party", "np");

		// ===========================================================
		// =================          Groups         =================
		// ===========================================================

		CommandSpec classes = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CLASSES_DESC))
				.permission("ntrpg.groups.list.classes")
				.executor((src, args) -> {
					IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
					Gui.showAvalaibleClasses(character);
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, classes, "classes");

		CommandSpec races = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CLASSES_RACE))
				.permission("ntrpg.groups.list.races")
				.executor((src, args) -> {
					IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
					Gui.sendRaceList(character);
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, races, "races");


		CommandSpec classgui = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_RACE_DESC))
				.arguments(new PlayerClassCommandElement(Text.of("class")))
				.executor((src, args) -> {
					args.<ConfigClass>getOne(Text.of("class")).ifPresent(o -> {
						IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
						Gui.showClassInfo(character, o);
					});
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, classgui, "class");


		CommandSpec racegui = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_RACE_DESC))
				.arguments(new RaceCommandElement(Text.of("race")))
				.executor((src, args) -> {
					args.<ConfigClass>getOne(Text.of("race")).ifPresent(o -> {
						IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
						Gui.showClassInfo(character, o);
					});
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, racegui, "race");



		CommandSpec weapon = CommandSpec.builder()
				.arguments(new AnyPlayerGroupCommandElement(Text.of("class_or_race")))
				.executor((src, args) -> {
					args.<PlayerGroup>getOne(Text.of("class_or_race"))
							.ifPresent(playerGroup -> {
								Player player = (Player) src;
								Gui.displayGroupWeapon(playerGroup, player);
							});
					return CommandResult.success();
				})
				.build();
		Sponge.getCommandManager().register(this, weapon, "weapons");

		CommandSpec armor = CommandSpec.builder()
				.arguments(new AnyPlayerGroupCommandElement(Text.of("class_or_race")))
				.executor((src, args) -> {

					args.<PlayerGroup>getOne(Text.of("class_or_race"))
							.ifPresent(playerGroup -> {
								Player player = (Player) src;
								Gui.displayGroupArmor(playerGroup, player);
							});
					return CommandResult.success();
				})
				.build();
		Sponge.getCommandManager().register(this, armor, "armor");



		CommandSpec runes = CommandSpec.builder()
				.permission("ntrpg.runes.list")
				.executor((src, args) -> {
					Gui.sendListOfRunes(GlobalScope.characterService.getCharacter((Player)src));
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, runes, "runes");
	}

	public void registerSkillCommands() {
		// ===========================================================
		// ===============          SKILL BIND         ===============
		// ===========================================================
		CommandSpec bind = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.skillbind")
				.arguments(
						GenericArguments.flags().valueFlag(GenericArguments
								.string(TextHelper.parse("lmb")), "l")
								.buildWith(new LearnedSkillCommandElement(TextHelper.parse("lmbskill"))),
						GenericArguments.flags().valueFlag(GenericArguments
								.string(TextHelper.parse("rmb")), "r")
								.buildWith(new LearnedSkillCommandElement(TextHelper.parse("rmbskill")))

				)
				.executor((src, args) -> {
					Optional<ISkill> lmb = args.getOne("lmb");

					Optional<ISkill> rmb = args.getOne("rmb");
					IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
					if (!character.getPlayer().getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
						ISkill r = rmb.orElse(null);
						ISkill l = lmb.orElse(null);

						ItemStack i = ItemStack.of(InventoryService.ITEM_SKILL_BIND, 1);
						NtRpgPlugin.GlobalScope.inventorySerivce.createHotbarSkill(i, r, l);
						character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, i);
					} else {
						character.getPlayer().sendMessage(TextHelper.parse(Localization.EMPTY_HAND_REQUIRED));
					}
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, bind, "bind", "nb");
	}
}
