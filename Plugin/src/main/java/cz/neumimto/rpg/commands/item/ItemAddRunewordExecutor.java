package cz.neumimto.rpg.commands.item;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class ItemAddRunewordExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		RuneWord r = args.<RuneWord>getOne("rw").get();
		Player p = (Player) src;
		Optional<ItemStack> itemInHand = p.getItemInHand(HandTypes.MAIN_HAND);
		if (itemInHand.isPresent()) {
			ItemStack itemStack = itemInHand.get();
			ItemStack itemStack1 = NtRpgPlugin.GlobalScope.runewordService.reBuildRuneword(itemStack, r);
			p.setItemInHand(HandTypes.MAIN_HAND, itemStack1);
		}
		return CommandResult.success();
	}
}
