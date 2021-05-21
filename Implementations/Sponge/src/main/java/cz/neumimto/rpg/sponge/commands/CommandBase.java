

package cz.neumimto.rpg.sponge.commands;

import cz.neumimto.rpg.api.effects.EffectSourceType;
import cz.neumimto.rpg.api.effects.IEffectSource;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public abstract class CommandBase implements CommandCallable, IEffectSourceProvider {

    protected String permission = "*";
    protected Optional<Text> help = Optional.empty();
    protected Text usage = Text.of("");
    protected List<String> alias = new ArrayList<>();

    public List<String> getAliases() {
        return alias;
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return Collections.emptyList();
    }

    @Override
    public boolean testPermission(CommandSource commandSource) {
        if (permission.equalsIgnoreCase("*")) {
            return true;
        }
        return commandSource.hasPermission(permission);
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource commandSource) {
        return Optional.of(Text.EMPTY);
    }

    @Override
    public Optional<Text> getHelp(CommandSource commandSource) {
        return help;
    }


    @Override
    public Text getUsage(CommandSource commandSource) {
        return usage;
    }

    @Override
    public IEffectSource getType() {
        return EffectSourceType.COMMAND;
    }
}
