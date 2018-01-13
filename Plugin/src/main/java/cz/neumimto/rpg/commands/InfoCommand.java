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
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.configuration.CommandPermissions;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import cz.neumimto.rpg.skills.SkillService;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 23.7.2015.
 */
@ResourceLoader.Command
public class InfoCommand extends CommandBase {

	@Inject
	private Logger logger;

	@Inject
	Game game;

	@Inject
	private CharacterService characterService;

	@Inject
	private SkillService skillService;

	@Inject
	private NtRpgPlugin plugin;

	@Inject
	private GroupService groupService;

	@Inject
	private RWService rwService;

	public InfoCommand() {
		setHelp(CommandLocalization.PLAYERINFO_HELP);
		setPermission(CommandPermissions.COMMANDINFO_PERMS);
		setDescription(CommandLocalization.PLAYERINFO_DESC);
		setUsage(CommandLocalization.PLAYERINFO_USAGE);
		addAlias(CommandPermissions.COMMANDINFO_ALIAS);
	}

	@Override
	public CommandResult process(CommandSource commandSource, String s) throws CommandException {
		final String[] args = s.split(" ");
		if (args.length == 0) {
			commandSource.sendMessage(getUsage(commandSource));
			return CommandResult.empty();
		}
		if (args[0].equalsIgnoreCase("player")) {
			if (args.length != 2) {
				commandSource.sendMessage(Text.of(getUsage(commandSource)));
				return CommandResult.success();
			}
			Optional<Player> o = game.getServer().getPlayer(args[1]);
			if (o.isPresent()) {
				Player player = o.get();
				if (player != commandSource && !player.hasPermission("list.character.others")) {
					player.sendMessage(Text.of(Localization.NO_PERMISSIONS));
					return CommandResult.empty();
				}
				printPlayerInfo(commandSource, args, player);
				return CommandResult.success();
			} else {
				commandSource.sendMessage(TextHelper.parse(Localization.PLAYER_IS_OFFLINE_MSG));
			}
		} else if (args[0].equalsIgnoreCase("character")) {
			if (!(commandSource instanceof Player)) {
				if (args.length != 2) {
					Player player = (Player) commandSource;
					IActiveCharacter target = characterService.getCharacter(player.getUniqueId());
					Gui.showCharacterInfo(target, target);
				}
			}
		} else if (args[0].equalsIgnoreCase("characters")) {
			if (!(commandSource instanceof Player)) {
				if (args.length != 2) {
					Player player = (Player) commandSource;
					IActiveCharacter target = characterService.getCharacter(player.getUniqueId());
					Gui.sendListOfCharacters(target, target.getCharacterBase());
				}
			}
		} else if (args[0].equalsIgnoreCase("runeword")) {
			Player player = (Player) commandSource;
			if (args.length == 2) {
				RuneWord rw = rwService.getRuneword(args[1]);
				if (rw != null) {
					Gui.displayRuneword(characterService.getCharacter(player.getUniqueId()), rw);
				}
			} else if (args.length == 3) {
				RuneWord rw = rwService.getRuneword(args[1]);
				if (rw != null) {
					IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
					String a = args[2];
					if (a.equalsIgnoreCase("allowed-items")) {
						Gui.displayRunewordAllowedItems(character, rw);
					} else if (a.equalsIgnoreCase("allowed-groups")) {
						Gui.displayRunewordAllowedGroups(character, rw);
					} else if (a.equalsIgnoreCase("required-groups")) {
						Gui.displayRunewordRequiredGroups(character, rw);
					} else if (a.equalsIgnoreCase("blocked-groups")) {
						Gui.displayRunewordBlockedGroups(character, rw);
					}
				}
			}
		} else if (args[0].equalsIgnoreCase("attributes-initial")) {
			PlayerGroup byName = groupService.getByName(args[1]);
			if (byName == null) {
				return CommandResult.empty();
			}
			Gui.displayInitialAttributes(byName, (Player) commandSource);
		} else if (args[0].equalsIgnoreCase("properties-initial")) {
			PlayerGroup byName = groupService.getByName(args[1]);
			if (byName == null) {
				return CommandResult.empty();
			}
			Gui.displayInitialProperties(byName, (Player) commandSource);
		} else if (args[0].equalsIgnoreCase("stats")) {
			Player player = (Player) commandSource;
			IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
			if (!character.isStub()) {
				Gui.sendStatus(character);
			} else {
				player.sendMessage(Text.of(Localization.CHARACTER_IS_REQUIRED));

			}
		} else if (args[0].equalsIgnoreCase("skilltree")) {
			IActiveCharacter character = characterService.getCharacter(((Player) commandSource).getUniqueId());
			Gui.openSkillTreeMenu(character, character.getPrimaryClass().getConfigClass().getSkillTree());
		} else {
			commandSource.sendMessage(getUsage(commandSource));
		}
		return CommandResult.success();
	}


	private void printPlayerInfo(CommandSource commandSource, String[] args, Player player) {
		NtRpgPlugin.asyncExecutor.execute(() -> {
			List<CharacterBase> characters = characterService.getPlayersCharacters(player.getUniqueId());
			if (characters.isEmpty()) {
				commandSource.sendMessage(Text.of("Player has no characters"));
				return;
			}
			for (CharacterBase character : characters) {
				Text build = Text.builder()
						.append(Text.of(character.getName()))
						.onHover(TextActions.showText(Text.of(getSmallInfo(character))))
						.build();
				commandSource.sendMessage(build);
			}
		});
	}

	private String getSmallInfo(CharacterBase character) {
		return TextColors.GOLD + ", C:" + character.getPrimaryClass() + ", R: " + character.getRace() + ", G: " + character.getGuildid();
	}


}
