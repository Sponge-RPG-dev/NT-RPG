package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.groups.PlayerGroup;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * Created by NeumimTo on 17.11.2017.
 */
public class AnyPlayerGroupCommandElement extends CommandElement {

	public AnyPlayerGroupCommandElement(Text class_or_race) {
		super(class_or_race);
	}

	@Nullable
	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String k = args.next();
		return NtRpgPlugin.GlobalScope.groupService.getAll().stream()
				.filter(a -> source.hasPermission("ntrpg.groups." + a.getName().toLowerCase()))
				.filter(a -> a.getName().equalsIgnoreCase(k))
				.collect(Collectors.toList());
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		return NtRpgPlugin.GlobalScope.groupService.getAll().stream()
				.map(PlayerGroup::getName)
				.filter(a -> src.hasPermission("ntrpg.groups." + a.toLowerCase()))
				.collect(Collectors.toList());
	}
}
