package cz.neumimto.rpg.commands;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by NeumimTo on 30.4.2017.
 */
@ResourceLoader.Command
public class CommandHP extends CommandBase {

	@Inject
	private CharacterService characterService;



	public CommandHP() {
		addAlias("hp");
	}

	@Override
	public CommandResult process(CommandSource commandSource, String s) throws CommandException {
		if (commandSource instanceof Player) {
			final Player player = (Player) commandSource;
			IActiveCharacter character = characterService.getCharacter(player);
			Gui.displayHealth(character);
		}
		return CommandResult.empty();
	}
}
