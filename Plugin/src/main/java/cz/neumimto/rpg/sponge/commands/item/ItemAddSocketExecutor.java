package cz.neumimto.rpg.sponge.commands.item;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.inventory.sockets.SocketType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class ItemAddSocketExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player player = (Player) src;
		Optional<SocketType> type = args.getOne("type");
		if (type.isPresent()) {
			Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
			if (itemInHand.isPresent()) {
				ItemStack itemStack = NtRpgPlugin.GlobalScope.runewordService.createSocket(itemInHand.get(), type.get());
				player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
				return CommandResult.builder().affectedItems(1).build();
			}
			src.sendMessage(Localizations.NO_ITEM_IN_HAND.toText());
			return CommandResult.empty();
		}
		return CommandResult.empty();
	}
}
