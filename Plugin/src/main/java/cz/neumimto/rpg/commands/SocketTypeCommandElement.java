package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.inventory.SocketType;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SocketTypeCommandElement extends CommandElement {
    public SocketTypeCommandElement(Text skill) {
        super(skill);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String next = args.next();
        return SocketType.valueOf(next.toUpperCase());
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Arrays.asList(SocketType.values()).stream().map(SocketType::name).collect(Collectors.toList());
    }


}
