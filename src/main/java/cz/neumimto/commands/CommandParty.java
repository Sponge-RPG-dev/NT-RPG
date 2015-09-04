package cz.neumimto.commands;

import com.google.common.base.Optional;
import cz.neumimto.configuration.CommandLocalization;
import cz.neumimto.configuration.CommandPermissions;
import cz.neumimto.ioc.Command;
import cz.neumimto.ioc.Inject;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.parties.PartyService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

/**
 * Created NeumimTo ja on 2.9.2015.
 */
@Command
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
                    character.setPendingPartyInvite(null);
                }
            }
        }
        return CommandResult.empty();
    }
}
