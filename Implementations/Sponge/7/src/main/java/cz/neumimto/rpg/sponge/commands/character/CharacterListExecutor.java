package cz.neumimto.rpg.sponge.commands.character;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

@Singleton
public class CharacterListExecutor implements CommandExecutor {

    @Inject
    private SpongeCharacterService characterService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        IActiveCharacter character = characterService.getCharacter(((Player) src).getUniqueId());
        Gui.sendListOfCharacters(character, character.getCharacterBase());
        return CommandResult.success();
    }
}
