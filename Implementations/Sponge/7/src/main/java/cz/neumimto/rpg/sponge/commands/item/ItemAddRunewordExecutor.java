package cz.neumimto.rpg.sponge.commands.item;

import cz.neumimto.rpg.common.inventory.runewords.RuneWord;
import cz.neumimto.rpg.sponge.inventory.runewords.RWService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class ItemAddRunewordExecutor implements CommandExecutor {

    @Inject
    private RWService rwService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        RuneWord r = args.<RuneWord>getOne("rw").get();
        Player p = (Player) src;
        Optional<ItemStack> itemInHand = p.getItemInHand(HandTypes.MAIN_HAND);
        if (itemInHand.isPresent()) {
            ItemStack itemStack = itemInHand.get();
            ItemStack itemStack1 = rwService.reBuildRuneword(itemStack, r);
            p.setItemInHand(HandTypes.MAIN_HAND, itemStack1);
        }
        return CommandResult.success();
    }
}
