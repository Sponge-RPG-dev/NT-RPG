package cz.neumimto.rpg.sponge.commands.item;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.inventory.runewords.Rune;
import cz.neumimto.rpg.sponge.inventory.sockets.SocketTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

public class GiveRuneToPlayerExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Rune runee = args.<Rune>getOne("rune").get();
        Player player = (Player) src;

        ItemStack is = NtRpgPlugin.GlobalScope.runewordService.createRune(SocketTypes.RUNE, runee.getName());
        player.getInventory().offer(is);
        return CommandResult.success();
    }
}
