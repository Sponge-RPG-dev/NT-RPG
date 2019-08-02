package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CharacterWeaponsExecutor implements CommandExecutor {

    @Inject
    private SpongeCharacterService characterService;
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        int page = (int) args.getOne(Text.of("page")).orElse(0);
        Player player = (Player) src;
        IActiveCharacter character = characterService.getCharacter(player);
        Gui.displayCharacterWeapons(character, page);
        return CommandResult.success();
    }
}

