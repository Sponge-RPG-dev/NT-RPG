package cz.neumimto.dei.commands;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.dei.serivce.PlayerService;
import cz.neumimto.dei.serivce.WorldService;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;


@Command(permission = "dei.town.create")
@Singleton
public class CreateTownCommand implements CommandCallable {

    @Inject
    private PlayerService playerService;

    @Inject
    private WorldService worldService;

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (source instanceof Player) {
            Player player = (Player) source;
            if (worldService.isWorldEnabled(player.getWorld())) {

            }
            return CommandResult.success();
        }
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return false;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return null;
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return null;
    }

    @Override
    public Text getUsage(CommandSource source) {
        return null;
    }
}
