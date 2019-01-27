package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.LiteralText;

public class ClassTypeCommandElement extends PatternMatchingCommandElement {
    public ClassTypeCommandElement(LiteralText type) {
        super(type);
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        return NtRpgPlugin.pluginConfig.CLASS_TYPES;
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        return choice;
    }
}
