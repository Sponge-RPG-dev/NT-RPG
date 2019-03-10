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

package cz.neumimto.rpg.commands;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.commands.admin.AddEffectExecutor;
import cz.neumimto.rpg.commands.admin.AddExperienceExecutor;
import cz.neumimto.rpg.commands.admin.ExecuteSkillExecutor;
import cz.neumimto.rpg.commands.admin.InspectPropertyExecutor;
import cz.neumimto.rpg.commands.admin.InvoceExecutorExecutor;
import cz.neumimto.rpg.commands.admin.ReloadExecutor;
import cz.neumimto.rpg.commands.character.CharacterAttributeExecutor;
import cz.neumimto.rpg.commands.character.CharacterChooseClassExecutor;
import cz.neumimto.rpg.commands.character.CharacterCreateExecutor;
import cz.neumimto.rpg.commands.character.CharacterDeleteExecutor;
import cz.neumimto.rpg.commands.character.CharacterListExecutor;
import cz.neumimto.rpg.commands.character.CharacterShowClassesExecutor;
import cz.neumimto.rpg.commands.character.CharacterSkillBindExecutor;
import cz.neumimto.rpg.commands.character.CharacterSkillExecuteExecutor;
import cz.neumimto.rpg.commands.character.CharacterSkillLearnExecutor;
import cz.neumimto.rpg.commands.character.CharacterSkillRefundExecutor;
import cz.neumimto.rpg.commands.character.CharacterSkillUpgradeExecutor;
import cz.neumimto.rpg.commands.character.CharacterSwitchExecutor;
import cz.neumimto.rpg.commands.elements.AnyClassDefCommandElement;
import cz.neumimto.rpg.commands.elements.CharacterAttributeCommandElement;
import cz.neumimto.rpg.commands.elements.ClassDefCommandElement;
import cz.neumimto.rpg.commands.elements.ClassTypeCommandElement;
import cz.neumimto.rpg.commands.elements.GlobalEffectCommandElement;
import cz.neumimto.rpg.commands.elements.LearnedSkillCommandElement;
import cz.neumimto.rpg.commands.elements.PartyMemberCommandElement;
import cz.neumimto.rpg.commands.elements.PlayerClassCommandElement;
import cz.neumimto.rpg.commands.elements.RuneCommandElement;
import cz.neumimto.rpg.commands.elements.UnlearnedSkillCommandElement;
import cz.neumimto.rpg.commands.item.GiveRuneToPlayerExecutor;
import cz.neumimto.rpg.commands.item.InspectItemDamageExecutor;
import cz.neumimto.rpg.commands.item.ItemAddGlobalEffectExecutor;
import cz.neumimto.rpg.commands.item.ItemAddGroupRestrictionExecutor;
import cz.neumimto.rpg.commands.item.ItemAddMetaExecutor;
import cz.neumimto.rpg.commands.item.ItemAddRarityExecutor;
import cz.neumimto.rpg.commands.item.ItemAddRunewordExecutor;
import cz.neumimto.rpg.commands.item.ItemAddSocketExecutor;
import cz.neumimto.rpg.commands.item.ItemAddTypeExecutor;
import cz.neumimto.rpg.commands.party.PartyAcceptExecutor;
import cz.neumimto.rpg.commands.party.PartyCreateExecutor;
import cz.neumimto.rpg.commands.party.PartyInviteExecutor;
import cz.neumimto.rpg.commands.party.PartyKickExecutor;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.items.ItemMetaType;
import cz.neumimto.rpg.inventory.sockets.SocketType;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class CommandService {

	@Inject
	private NtRpgPlugin plugin;

	@Inject
	private GlobalScope globalScope;

	public void registerStandartCommands() {
		registerAdminCommands();
		registerCharacterCommands();
		registerItemCommands();
	}

	private void registerAdminCommands() {

		//==========SKILLS AND EFFECTS==========

		CommandSpec executeSkill = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_EXEC_SKILL_DESC))
				.arguments(
						GenericArguments.catalogedElement(Text.of("skill"), ISkill.class),
						GenericArguments.flags().valueFlag(GenericArguments
								.integer(TextHelper.parse("level")), "l")
								.buildWith(GenericArguments.none())
				)
				.executor(new ExecuteSkillExecutor())
				.build();

		CommandSpec addEffect = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_EFFECT_ADD))
				.arguments(
						GenericArguments.onlyOne(GenericArguments.player(TextHelper.parse("player"))),
						new GlobalEffectCommandElement(Text.of("effect")),
						GenericArguments.longNum(TextHelper.parse("duration")),
						GenericArguments.remainingJoinedStrings(TextHelper.parse("data"))
				)
				.executor(new AddEffectExecutor())
				.build();

		//=========CHARACTER MANIPULATIONS=========

		CommandSpec addExperience = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_EXP_ADD))
				.arguments(
						GenericArguments.onlyOne(GenericArguments.player(TextHelper.parse("player"))),
						GenericArguments.remainingJoinedStrings(TextHelper.parse("data"))
				)
				.executor(new AddExperienceExecutor())
				.build();

		CommandSpec exp = CommandSpec.builder()
				.child(addExperience, "add")
				.build();

		//==========UTILITY==========

		CommandSpec reload = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_RELOAD))
				.arguments(GenericArguments.remainingJoinedStrings(TextHelper.parse("args")))
				.executor(new ReloadExecutor())
				.build();

		CommandSpec inspectProperty = CommandSpec.builder()
				.arguments(
						GenericArguments.onlyOne(GenericArguments.player(TextHelper.parse("player"))),
						GenericArguments.remainingJoinedStrings(TextHelper.parse("data"))
				)
				.executor(new InspectPropertyExecutor())
				.build();

		CommandSpec inspectItemDamage = CommandSpec.builder()
				.arguments(
						GenericArguments.onlyOne(GenericArguments.player(TextHelper.parse("player")))
				)
				.executor(new InspectItemDamageExecutor())
				.build();

		CommandSpec inspect = CommandSpec.builder()
				.child(inspectProperty, "property", "p")
				.child(inspectItemDamage, "itemDamage", "idmg")
				.build();


		CommandSpec invoke = CommandSpec.builder()
				.description(Text.builder("Command which let you execute commands as another player.")
						.append(Text.builder("/nadmin invoke SomePlayer classes Primary").color(TextColors.LIGHT_PURPLE).build())
						.append(Text.builder("will class selection GUI on the client side of the player SomePlayer. Does not bypass any permissions.").build()).build())
				.arguments(
						GenericArguments.player(Text.of("player")),
						GenericArguments.remainingJoinedStrings(Text.of("command"))
				)
				.executor(new InvoceExecutorExecutor())
				.build();


		CommandSpec adminRoot = CommandSpec
				.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_DESC))
				.permission("ntrpg.admin")
				.child(executeSkill, "skill", "s")
				.child(addEffect, "effect", "ef")

				.child(exp, "experiences", "exp")

				.child(reload, "reload")
				.child(inspect, "i", "inspect")
				.child(invoke, "invoke", "invk")
				.build();

		Sponge.getCommandManager().register(plugin, adminRoot, "nadmin", "na");
	}

	private void registerCharacterCommands() {

		//==========CHARACTER MANIPULATION==========

		CommandSpec createCharacter = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_CREATE_DESCRIPTION))
				.arguments(
						GenericArguments.remainingJoinedStrings(TextHelper.parse("name"))
				)
				.permission("ntrpg.player.character.create")
				.executor(new CharacterCreateExecutor())
				.build();

		CommandSpec deleteCharacter = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_DELETE_DESCRIPTION))
				.arguments(
						GenericArguments.remainingJoinedStrings(TextHelper.parse("name"))
				)
				.permission("ntrpg.player.character.delete")
				.executor(new CharacterDeleteExecutor())
				.build();

		CommandSpec cswitch = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.arguments(
						GenericArguments.remainingJoinedStrings(Text.of("name"))
				)
				.executor(new CharacterSwitchExecutor())
				.build();

		CommandSpec cslist = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_CHARACTE_LIST))
				.executor(new CharacterListExecutor())
				.build();

		//==========CHARACTER CLASS==========

		CommandSpec setclass = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.arguments(
						new AnyClassDefCommandElement(TextHelper.parse("class"))
				)
				.permission("ntrpg.player.set.class")
				.executor(new CharacterChooseClassExecutor())
				.build();


		CommandSpec cset = CommandSpec.builder()
				.child(setclass, "class", "c")
				.build();

		//==========CHARACTER SKILLS==========

		CommandSpec learn = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_SKILL_LEARN))
				.arguments(
						new UnlearnedSkillCommandElement(Text.of("skill")),
						new PlayerClassCommandElement(Text.of("class"))
				)
				.permission("ntrpg.player.skills")
				.executor(new CharacterSkillLearnExecutor())
				.build();

		CommandSpec upgrade = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_SKILL_UPGRADE))
				.arguments(
						new UnlearnedSkillCommandElement(Text.of("skill")),
						new PlayerClassCommandElement(Text.of("class"))
				)
				.permission("ntrpg.player.skills")
				.executor(new CharacterSkillUpgradeExecutor())
				.build();

		CommandSpec refund = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_SKILL_REFUND))
				.arguments(
						new UnlearnedSkillCommandElement(Text.of("skill")),
						new PlayerClassCommandElement(Text.of("class"))
				)
				.permission("ntrpg.player.skills.refund")
				.executor(new CharacterSkillRefundExecutor())
				.build();

		CommandSpec cskill = CommandSpec.builder()
				.child(learn, "learn", "l")
				.child(upgrade, "upgrade", "u")
				.child(refund, "refund", "r")
				.build();

		//==========CHARACTER ATTRIBUTES==========

		CommandSpec cattribute = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_ATTRIBUTE))
				.arguments(new CharacterAttributeCommandElement(Text.of("attribute")),
						GenericArguments.integer(Text.of("amount")))
				.executor(new CharacterAttributeExecutor())
				.build();

		CommandSpec characterRoot = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.child(createCharacter, "create", "c")
				.child(deleteCharacter, "remove", "rm", "delete")
				.child(cswitch, "switch")
				.child(cslist, "list")

				.child(cset, "set", "s")

				.child(cskill, "skill", "s", "sk")

				.child(cattribute, "attribute", "attr", "a")
				.executor(new CharacterListExecutor()) //default fallback for char list
				.build();

		Sponge.getCommandManager().register(plugin, characterRoot, "character", "char", "nc");

		//==========SKILLS==========

		CommandSpec skillexecute = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_HP_DESC))
				.arguments(
						new LearnedSkillCommandElement(TextHelper.parse("skill"))
				)
				.executor(new CharacterSkillExecuteExecutor())
				.build();

		Sponge.getCommandManager().register(plugin, skillexecute, "skill", "skl", "ns");

		CommandSpec bind = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.skillbind")
				.arguments(
						new LearnedSkillCommandElement(TextHelper.parse("skill"))
				)
				.executor(new CharacterSkillBindExecutor())
				.build();

		Sponge.getCommandManager().register(plugin, bind, "bind", "nb");

		//==========PARTY==========

		CommandSpec createparty = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.party")
				.executor(new PartyCreateExecutor())
				.build();

		CommandSpec kick = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.party")
				.arguments(
						new PartyMemberCommandElement(TextHelper.parse("player"))
				)
				.executor(new PartyKickExecutor())
				.build();

		CommandSpec invite = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.party")
				.arguments(
						GenericArguments.player(TextHelper.parse("player"))
				)
				.executor(new PartyInviteExecutor())
				.build();

		CommandSpec accept = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_BIND_DESC))
				.permission("ntrpg.player.party")
				.executor(new PartyAcceptExecutor())
				.build();

		CommandSpec partyRoot = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_CHOOSE_DESC))
				.child(createparty, "create", "c")
				.child(kick, "kick", "k")
				.child(invite, "invite", "i")
				.child(accept, "accept", "a")
				.build();

		Sponge.getCommandManager().register(plugin, partyRoot, "party", "np", "nparty");

		//==========GROUPS==========

		CommandSpec classes = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_CLASSES_DESC))
				.arguments(
						GenericArguments.optional(new ClassTypeCommandElement(Text.of("type")))
				)
				.permission("ntrpg.classes.list")
				.executor(new CharacterShowClassesExecutor())
				.build();

		Sponge.getCommandManager().register(plugin, classes, "classes");

		CommandSpec classgui = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE
						.deserialize(CommandLocalization.COMMAND_RACE_DESC))
				.arguments(new AnyClassDefCommandElement(Text.of("class"), false))
				.executor((src, args) -> {
					args.<ClassDefinition>getOne(Text.of("class")).ifPresent(o -> {
						IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
						Gui.showClassInfo(character, o);
					});
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(plugin, classgui, "class");

		CommandSpec weapon = CommandSpec.builder()
				.arguments(new ClassDefCommandElement(Text.of("class")))
				.executor((src, args) -> {
					args.<ClassDefinition>getOne(Text.of("class"))
							.ifPresent(playerGroup -> {
								Player player = (Player) src;
								Gui.displayClassWeapons(playerGroup, player);
							});
					return CommandResult.success();
				})
				.build();
		Sponge.getCommandManager().register(plugin, weapon, "weapons", "wp");

		CommandSpec armor = CommandSpec.builder()
				.arguments(new ClassDefCommandElement(Text.of("class")))
				.executor((src, args) -> {

					args.<ClassDefinition>getOne(Text.of("class"))
							.ifPresent(playerGroup -> {
								Player player = (Player) src;
								Gui.displayClassArmor(playerGroup, player);
							});
					return CommandResult.success();
				})
				.build();
		Sponge.getCommandManager().register(plugin, armor, "armor");

		CommandSpec runes = CommandSpec.builder()
				.permission("ntrpg.runes.list")
				.executor((src, args) -> {
					Gui.sendListOfRunes(NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src));
					return CommandResult.success();
				})
				.build();

		Sponge.getCommandManager().register(plugin, runes, "runes");
	}

	private void registerItemCommands() {
		//==========ITEMS==========

		CommandSpec itemAddGlobalEffect = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_ENCHANT_ADD))
				.arguments(
						new GlobalEffectCommandElement(TextHelper.parse("effect")),
						GenericArguments.remainingJoinedStrings(TextHelper.parse("params"))
				)
				.executor(new ItemAddGlobalEffectExecutor())
				.build();


		CommandSpec itemEnchant = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_ENCHANT))
				.child(itemAddGlobalEffect, "add", "e")
				.build();


		CommandSpec itemAddSocket = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_SOCKET))
				.arguments(
						GenericArguments.catalogedElement(Text.of("type"), SocketType.class)
				)
				.executor(new ItemAddSocketExecutor())
				.build();

		CommandSpec itemAddRune = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_RUNE))
				.arguments(
						new RuneCommandElement(TextHelper.parse("rune"))
				)
				.executor(new GiveRuneToPlayerExecutor())
				.build();

		CommandSpec itemAddRarity = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_RARITY))
				.arguments(
						GenericArguments.integer(TextHelper.parse("level"))
				)
				.executor(new ItemAddRarityExecutor())
				.build();

		CommandSpec itemAddMeta = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_RARITY))
				.arguments(
						GenericArguments.text(Text.of("meta"), TextSerializers.FORMATTING_CODE, true)
				)
				.executor(new ItemAddMetaExecutor())
				.build();

		CommandSpec itemAddType = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_ITEM_TYPE))
				.arguments(
						GenericArguments.catalogedElement(Text.of("type"), ItemMetaType.class)
				)
				.executor(new ItemAddTypeExecutor())
				.build();

		CommandSpec itemAddGroupRestriction = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_RARITY))
				.arguments(
						new ClassDefCommandElement(Text.of("group")),
						GenericArguments.integer(Text.of("level"))
				)
				.executor(new ItemAddGroupRestrictionExecutor())
				.build();

		CommandSpec itemAddRuneword = CommandSpec.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_RUNEWORD))
				.arguments(
						new RuneCommandElement(TextHelper.parse("rw"))
				)
				.executor(new ItemAddRunewordExecutor())
				.build();

		CommandSpec itemRoot = CommandSpec
				.builder()
				.description(TextSerializers.FORMATTING_CODE.deserialize(CommandLocalization.COMMAND_ADMIN_DESC))
				.permission("ntrpg.item")
				.child(itemEnchant, "enchant", "e")
				.child(itemAddSocket, "socket", "sk")
				.child(itemAddRune, "rune", "r")
				.child(itemAddRuneword, "runeword", "rw")
				.child(itemAddRarity, "rarity", "rrty")
				.child(itemAddMeta, "itemmeta", "imeta", "imt")
				.child(itemAddGroupRestriction, "grouprequirements", "gr")
				.child(itemAddType, "itemType", "it", "type")
				.build();

		Sponge.getCommandManager().register(plugin, itemRoot, "nitem", "item");
	}

	public void registerCommand(CommandBase commandCallable) {
		try {
			Sponge.getCommandManager().register(plugin, commandCallable, commandCallable.getAliases());
		} catch (NoSuchMethodError e) {
			try {
				Object o = Sponge.class.getDeclaredMethod("getCommandDispatcher").invoke(null);
				o.getClass().getDeclaredMethod("register", Object.class, CommandCallable.class, List.class)
						.invoke(o, plugin, commandCallable, commandCallable.getAliases());
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
				e1.printStackTrace();
			}
		}
	}
}
