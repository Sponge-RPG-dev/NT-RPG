package cz.neumimto.rpg.sponge.commands.admin;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class InvoceExecutorExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		args.<Player>getOne(Text.of("player")).ifPresent(p -> {
			args.<String>getOne(Text.of("command")).ifPresent(c -> {
				Sponge.getCommandManager().process(p, c);
			});
		});
		return CommandResult.success();
	}
}
