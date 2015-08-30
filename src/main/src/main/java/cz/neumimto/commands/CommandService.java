package cz.neumimto.commands;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
import org.spongepowered.api.Game;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class CommandService {

    @Inject
    private Game game;

    @Inject
    private NtRpgPlugin plugin;

    public void registerCommand(CommandBase commandCallable) {
        game.getCommandDispatcher().register(plugin, commandCallable, commandCallable.getAliases());
    }


}
