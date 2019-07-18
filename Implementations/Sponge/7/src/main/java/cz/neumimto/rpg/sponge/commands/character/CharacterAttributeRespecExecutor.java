package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CharacterAttributeRespecExecutor implements CommandExecutor {

    @Inject
    private SpongeCharacterService characterServise;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ISpongeCharacter character = characterServise.getCharacter((Player) src);
        characterServise.resetAttributes(character);
        characterServise.putInSaveQueue(character.getCharacterBase());
        return CommandResult.success();
    }

}
