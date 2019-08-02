package cz.neumimto.rpg.sponge.commands.party;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

@Singleton
public class PartyKickExecutor implements CommandExecutor {
    
    @Inject
    private SpongeCharacterService spongeCharacterService;
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        args.<IActiveCharacter>getOne(TextHelper.parse("player")).ifPresent(o -> {
            ISpongeCharacter character = spongeCharacterService.getCharacter((Player) src);
            Rpg.get().getPartyService().kickCharacterFromParty(character.getParty(), character);
        });
        return CommandResult.success();
    }
}
