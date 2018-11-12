package cz.neumimto.rpg.commands;


import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.CommandLocalization;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by NeumimTo on 29.4.2017.
 */
@ResourceLoader.Command
public class CommandSetHealthScale extends CommandBase {

	@Inject
	private CharacterService characterService;

	public CommandSetHealthScale() {
		addAlias("healthscale");
		addAlias("hs");
		setUsage("/healthscale [integer]");
		setDescription(CommandLocalization.COMMAND_SET_HEALTHSCALE_DESCRIPTION);
	}

	@Override
	public CommandResult process(CommandSource commandSource, String s) throws CommandException {
		if (commandSource instanceof Player) {
			Player pl = (Player) commandSource;
			IActiveCharacter character = characterService.getCharacter(pl.getUniqueId());
			if (character.isStub()) {
				return CommandResult.empty();
			}
			if (s != null) {
				characterService.setHeathscale(character, Double.parseDouble(s));
			}
		}
		return CommandResult.success();
	}
}
