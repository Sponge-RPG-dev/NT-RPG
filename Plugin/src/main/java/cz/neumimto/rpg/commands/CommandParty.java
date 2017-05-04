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
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.configuration.CommandPermissions;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.parties.PartyService;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

/**
 * Created NeumimTo on 2.9.2015.
 */
@ResourceLoader.Command
public class CommandParty extends CommandBase {

    @Inject
    private CharacterService characterService;

    @Inject
    private Game game;

    @Inject
    private PartyService partyService;

    public CommandParty() {
        addAlias("nparty");
        addAlias(CommandPermissions.COMMAND_PARTY_ALIAS);
        setUsage(CommandLocalization.COMMAND_PARTY_USAGE);
        setDescription(CommandLocalization.COMMAND_PARTY_DESCRIPTION);
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        String[] args = s.split(" ");
        if (args.length == 0) {
            commandSource.sendMessage(getUsage(commandSource));
            return CommandResult.empty();
        }
        if (commandSource instanceof Player) {
            final Player player = (Player) commandSource;
            if (args[0].equalsIgnoreCase("kick")) {
                IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
                if (character.isPartyLeader()) {
                    Optional<Player> target = game.getServer().getPlayer(args[1]);
                    if (target.isPresent()) {
                        IActiveCharacter chartarget = characterService.getCharacter(target.get().getUniqueId());
                        if (chartarget.isInPartyWith(character)) {
                            partyService.kickCharacterFromParty(character.getParty(), chartarget);
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("invite")) {
                IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
                if (!character.isPartyLeader()) {
                    return CommandResult.success();
                }
                if (character.isPartyLeader()) {
                    Optional<Player> target = game.getServer().getPlayer(args[1]);
                    if (target.isPresent()) {
                        IActiveCharacter tcharacter = characterService.getCharacter(target.get().getUniqueId());
                        partyService.sendPartyInvite(character.getParty(), tcharacter);
                    }
                }
            } else if (args[0].equals("accept")) {
                IActiveCharacter character = characterService.getCharacter(player.getUniqueId());
                if (character.getPendingPartyInvite() != null) {
                    partyService.addToParty(character.getPendingPartyInvite(), character);
                }
            }
        }
        return CommandResult.empty();
    }
}
