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

package cz.neumimto.commands;

import cz.neumimto.GroupService;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ResourceLoader;
import cz.neumimto.configuration.CommandLocalization;
import cz.neumimto.configuration.CommandPermissions;
import cz.neumimto.configuration.Localization;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.gui.Gui;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.groups.Guild;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.groups.Race;
import cz.neumimto.players.parties.Party;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;

/**
 * Created by NeumimTo on 23.7.2015.
 */
@ResourceLoader.Command
public class CommandCreate extends CommandBase {

    @Inject
    CharacterService characterService;

    @Inject
    Game game;

    @Inject
    NtRpgPlugin plugin;

    @Inject
    GroupService groupService;

    public CommandCreate() {
        addAlias(CommandPermissions.COMMAND_CREATE_ALIAS);
        setUsage(CommandLocalization.COMMAND_CREATE_USAGE);
        setDescription(CommandLocalization.COMMAND_CREATE_DESCRIPTION);
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        if (commandSource instanceof Player) {
            String[] args = s.split(" ");
            if (args.length != 2) {
                commandSource.sendMessage(Texts.of(getUsage(commandSource)));
                return CommandResult.empty();
            }
            if (args[0].equalsIgnoreCase("character")) {
                game.getScheduler().createTaskBuilder().async().execute(() -> {
                    Player player = (Player) commandSource;
                    int i = characterService.canCreateNewCharacter(player.getUniqueId());
                    if (i == 1) {
                        commandSource.sendMessage(Texts.of(Localization.REACHED_CHARACTER_LIMIT));
                    } else if (i == 2) {
                        commandSource.sendMessage(Texts.of(Localization.CHARACTER_EXISTS));
                    } else if (i == 0) {
                        CharacterBase characterBase = new CharacterBase();
                        characterBase.setName(args[1]);
                        characterBase.setGuild(Guild.Default.getName());
                        characterBase.setRace(Race.Default.getName());
                        characterBase.setPrimaryClass(NClass.Default.getName());
                        characterBase.setUuid(player.getUniqueId());
                        characterBase.setLevel(1);
                        characterBase.setSkillPoints(PluginConfig.SKILLPOINTS_ON_START);
                        characterBase.setAttributePoints(PluginConfig.ATTRIBUTEPOINTS_ON_START);
                        characterService.save(characterBase);
                        IActiveCharacter character = characterService.buildActiveCharacterAsynchronously(player, characterBase);
                        characterService.setActiveCharacterSynchronously(player.getUniqueId(), character);
                        commandSource.sendMessage(Texts.of(CommandLocalization.CHARACTER_CREATED.replaceAll("%1", characterBase.getName())));
                    }
                }).submit(plugin);
            } else if (args[0].equalsIgnoreCase("party")) {
                IActiveCharacter character = characterService.getCharacter(((Player) commandSource).getUniqueId());
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
            }
        }
        return CommandResult.success();
    }
}
