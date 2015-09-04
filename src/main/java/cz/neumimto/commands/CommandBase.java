package cz.neumimto.commands;

import com.google.common.base.Optional;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by NeumimTo on 22.7.2015.
 */

public abstract class CommandBase implements CommandCallable {

    protected String permission = "*";
    protected Optional<Text> shortDescription = Optional.absent();
    protected Optional<Text> help = Optional.absent();
    protected Text usage = Texts.of("");
    protected List<String> alias = new ArrayList();

    public List<String> getAliases() {
        return alias;
    }

    @Override
    public CommandResult process(CommandSource commandSource, String s) throws CommandException {
        return CommandResult.empty();
    }

    protected void setDescription(String string) {
        /* Ty for epic wrapers use... */
        shortDescription = Optional.of(Texts.of(string));
    }

    protected void setHelp(String string) {
        help = Optional.of(Texts.of(string));
    }

    protected void setUsage(String string) {
        usage = Texts.of(string);
    }

    protected void addAlias(String string) {
        alias.add(string);
    }

    protected void setPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public List<String> getSuggestions(CommandSource commandSource, String s) throws CommandException {
        return Collections.emptyList();
    }

    @Override
    public boolean testPermission(CommandSource commandSource) {
        if (permission.equalsIgnoreCase("*"))
            return true;
        return commandSource.hasPermission(permission);
    }

    @Override
    public Optional<? extends Text> getShortDescription(CommandSource commandSource) {
        return shortDescription;
    }

    @Override
    public Optional<? extends Text> getHelp(CommandSource commandSource) {
        return help;
    }

    @Override
    public Text getUsage(CommandSource commandSource) {
        return usage;
    }
}
