package cz.neumimto.rpg.sponge.commands.item;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

public class ItemAddRarityExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Integer integer = args.<Integer>getOne("level").get();
        Set<Integer> i = new HashSet<>();
        for (String s : pluginConfig.ITEM_RARITY) {
            i.add(Integer.parseInt(s.split(",")[0]));
        }
        if (!i.contains(integer)) {
            src.sendMessage(Text.builder("Unknown rarity value").color(TextColors.RED).build());
            return CommandResult.empty();
        }


        Player player = (Player) src;

        Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
        if (!itemInHand.isPresent()) {
            src.sendMessage(Text.builder("No item in main hand").color(TextColors.RED).build());
            return CommandResult.empty();
        }

        ItemStack itemStack = itemInHand.get();

        NtRpgPlugin.GlobalScope.inventorySerivce.setItemRarity(itemInHand.get(), integer);
        NtRpgPlugin.GlobalScope.inventorySerivce.createItemMetaSectionIfMissing(itemStack);
        NtRpgPlugin.GlobalScope.inventorySerivce.updateLore(itemStack);
        player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
        return CommandResult.success();
    }
}
