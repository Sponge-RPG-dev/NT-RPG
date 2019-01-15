package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.LiteralText;

import javax.annotation.Nullable;
import java.util.List;

public class PlayerClassTypeCommandElement extends CommandElement {
    public PlayerClassTypeCommandElement(LiteralText type) {
        super(type);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String clazz = args.next();
        if (NtRpgPlugin.pluginConfig.CLASS_TYPES.contains(clazz)) {
            return clazz;
        }
        throw args.createError(Localizations.NO_SUCH_CLASSTYPE.toText());
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return NtRpgPlugin.pluginConfig.CLASS_TYPES;
    }
}
