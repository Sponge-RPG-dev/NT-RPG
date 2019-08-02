package cz.neumimto.rpg.sponge.commands.item;

import cz.neumimto.rpg.common.inventory.crafting.runewords.Rune;
import cz.neumimto.rpg.common.inventory.sockets.SocketTypes;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GiveRuneToPlayerExecutor implements CommandExecutor {

    @Inject
    private RWService rwService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Rune runee = args.<Rune>getOne("rune").get();
        Player player = (Player) src;

        ItemStack is = rwService.createRune(SocketTypes.RUNE, runee.getName());
        player.getInventory().offer(is);
        return CommandResult.success();
    }
}
