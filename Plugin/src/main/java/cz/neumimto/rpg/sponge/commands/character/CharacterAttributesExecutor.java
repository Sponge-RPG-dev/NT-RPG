package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class CharacterAttributesExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player);
        Gui.displayCharacterAttributes(character);
        return CommandResult.success();
    }
}
