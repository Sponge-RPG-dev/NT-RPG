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
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.Race;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.players.properties.PropertyService;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.FileUtils;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
	public static final String namedCause = "ntrpg";

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
						new AnySkillCommandElement(Text.of("skill")),
						GenericArguments.flags().valueFlag(GenericArguments
								.integer(Text.of("level")), "l")
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
						new GlobalEffectCommandElement(Text.of("effect")),
						GenericArguments.remainingJoinedStrings(Text.of("params"))
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
						player.sendMessage(Text.of("Enchantment " + effect.getName() + " added"));
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
						new GlobalEffectCommandElement(Text.of("effect")),
						GenericArguments.remainingJoinedStrings(Text.of("args")))
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
						GenericArguments.onlyOne(GenericArguments.integer(Text.of("count")))
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
						new RuneCommandElement(Text.of("rune"))
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
						new RuneCommandElement(Text.of("rw"))
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
						GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
						GenericArguments.remainingJoinedStrings(Text.of("data"))
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
				.arguments(GenericArguments.remainingJoinedStrings(Text.of("args")))
				.executor((src, args) -> {
					String[] a = args.<String>getOne("args").get().split(" ");
					if (a.length == 1) {
						src.sendMessage(Text.of("js[s/a/g] skilltree [r,a]"));
						return CommandResult.empty();
					}
					if (a[1].equalsIgnoreCase("js")) {
						if (!PluginConfig.DEBUG) {
							src.sendMessage(Text.of("Reloading is allowed only in debug mode"));
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
		SpongeExecutorService asyncExecutor = Sponge.getGame().getScheduler().createAsyncExecutor(NtRpgPlugin.this);

		// ===========================================================
		// ==============           CHAR CREATE         ==============
		// ===========================================================

		CommandSpec createCharacter = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CREATE_DESCRIPTION))
				.arguments(GenericArguments.remainingJoinedStrings(Text.of("name")))
				.permission("ntrpg.player.character.create")
				.executor((src, args) -> {
					String a = args.<String>getOne("name").get();
					CompletableFuture.runAsync(() -> {
						Player player = (Player) src;
						CharacterService characterService = IoC.get().build(CharacterService.class);
						int i = characterService.canCreateNewCharacter(player.getUniqueId(), a);
						if (i == 1) {
							src.sendMessage(Text.of(Localization.REACHED_CHARACTER_LIMIT));
						} else if (i == 2) {
							src.sendMessage(Text.of(Localization.CHARACTER_EXISTS));
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
							src.sendMessage(Text.of(CommandLocalization.CHARACTER_CREATED.replaceAll("%1", characterBase.getName())));
							Gui.sendListOfCharacters(characterService.getCharacter(player.getUniqueId()), characterBase);
						}
					}, asyncExecutor);
					return CommandResult.success();
				})
				.build();

		CommandSpec characterRoot = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.child(createCharacter, "create", "c")
				.build();


		Sponge.getCommandManager().register(this, characterRoot, "character", "char", "nc");

		// ===========================================================
		// =================          SKILLS         =================
		// ===========================================================

		CommandSpec skillexecute = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_HP_DESC))
				.arguments(new LearnedSkillCommandElement(Text.of("skill")))
				.executor((src, args) -> {
					IActiveCharacter character = GlobalScope.characterService.getCharacter((Player) src);
					args.<ISkill>getOne(Text.of("skill")).ifPresent(iSkill -> {
						ExtendedSkillInfo info = character.getSkillInfo(iSkill.getName());
						if (info == ExtendedSkillInfo.Empty || info == null) {
							src.sendMessage(Text.of(Localization.CHARACTER_DOES_NOT_HAVE_SKILL));
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
				.arguments(new PartyMemberCommandElement(Text.of("player")))
				.executor((src, args) -> {
					args.<IActiveCharacter>getOne(Text.of("player")).ifPresent(o -> {

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
				.arguments(GenericArguments.player(Text.of("player")))
				.executor((src, args) -> {
					args.<Player>getOne(Text.of("player")).ifPresent(o -> {
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
								.string(Text.of("lmb")), "l")
								.buildWith(new LearnedSkillCommandElement(Text.of("lmbskill"))),
						GenericArguments.flags().valueFlag(GenericArguments
								.string(Text.of("rmb")), "r")
								.buildWith(new LearnedSkillCommandElement(Text.of("rmbskill")))

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
						character.getPlayer().sendMessage(Text.of(Localization.EMPTY_HAND_REQUIRED));
					}
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(this, bind, "bind", "nb");
	}
}
