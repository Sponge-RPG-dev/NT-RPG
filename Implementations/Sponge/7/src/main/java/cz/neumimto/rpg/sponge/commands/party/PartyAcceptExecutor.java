package cz.neumimto.rpg.sponge.commands.party;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

@Singleton
public class PartyAcceptExecutor implements CommandExecutor {

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private SpongePartyService partyService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ISpongeCharacter character = characterService.getCharacter((Player) src);
        if (character.getPendingPartyInvite() != null) {
            partyService.addToParty(character.getPendingPartyInvite(), character);
        }
        return CommandResult.success();
    }
}
