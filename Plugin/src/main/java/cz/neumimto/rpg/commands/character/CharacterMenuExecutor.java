package cz.neumimto.rpg.commands.character;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class CharacterMenuExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player);
        Gui.displayCharacterMenu(character);
        return CommandResult.success();
    }
}
