package cz.neumimto.rpg.commands.admin;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;

public class AddExperienceExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Player player = args.<Player>getOne("player").get();
		String data = args.<String>getOne("data").get();
		IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
		Collection<PlayerClassData> classes = character.getClasses().values();
		String[] a = data.split(" ");
		for (PlayerClassData aClass : classes) {
			if (aClass.getClassDefinition().getName().equalsIgnoreCase(a[0])) {
				if (aClass.takesExp()) {
					NtRpgPlugin.GlobalScope.characterService.addExperiences(character, Double.valueOf(a[1]), aClass);
				}
			}
		}
		return CommandResult.success();
	}
}
