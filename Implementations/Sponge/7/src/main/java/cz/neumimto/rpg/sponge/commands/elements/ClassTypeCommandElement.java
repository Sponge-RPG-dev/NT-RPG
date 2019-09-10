package cz.neumimto.rpg.sponge.commands.elements;

import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.LiteralText;

public class ClassTypeCommandElement extends PatternMatchingCommandElement {
    public ClassTypeCommandElement(LiteralText type) {
        super(type);
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        return SpongeRpgPlugin.pluginConfig.CLASS_TYPES.keySet();
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        return choice;
    }
}
