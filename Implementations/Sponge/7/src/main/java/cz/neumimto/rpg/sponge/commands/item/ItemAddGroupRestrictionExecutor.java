package cz.neumimto.rpg.sponge.commands.item;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.sponge.inventory.SpongeInventoryService;
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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class ItemAddGroupRestrictionExecutor implements CommandExecutor {

    @Inject
    private SpongeInventoryService inventoryService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) src;
        Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
        if (!itemInHand.isPresent()) {
            src.sendMessage(Text.builder("No item in main hand").color(TextColors.RED).build());
            return CommandResult.empty();
        }
        ItemStack itemStack = itemInHand.get();
        ClassDefinition group = args.<ClassDefinition>getOne("group").get();
        Integer integer = args.<Integer>getOne("level").orElse(0);

        inventoryService.addGroupRestriction(itemStack, group, integer);
        inventoryService.updateLore(itemStack);
        player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
        return CommandResult.success();
    }
}
