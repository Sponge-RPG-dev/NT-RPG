package cz.neumito.rpg.rest.commands;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.commands.CommandBase;
import cz.neumito.rpg.rest.RestService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;


//@ResourceLoader.Command
public class ServerCommand extends CommandBase {

    @Inject
    private RestService restService;

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        if (commandSource instanceof Player) {

        } else {
            restart();
        }
        return CommandResult.success();
    }

    private void restart() {

    }
}
