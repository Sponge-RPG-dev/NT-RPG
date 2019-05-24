package cz.neumimto.rpg.sponge.commands.item;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.groups.ClassDefinition;
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

import java.util.Optional;

public class ItemAddGroupRestrictionExecutor implements CommandExecutor {
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

		NtRpgPlugin.GlobalScope.inventorySerivce.addGroupRestriction(itemStack, group, integer);
		NtRpgPlugin.GlobalScope.inventorySerivce.updateLore(itemStack);
		player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
		return CommandResult.success();
	}
}
