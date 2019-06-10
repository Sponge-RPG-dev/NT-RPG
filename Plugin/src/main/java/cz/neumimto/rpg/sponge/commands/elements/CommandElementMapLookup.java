package cz.neumimto.rpg.sponge.commands.elements;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public class CommandElementMapLookup extends PatternMatchingCommandElement {

    private final Supplier<Map<String, ?>> supplier;

    public CommandElementMapLookup(@Nullable Text key, Supplier<Map<String, ?>> supplier) {
        super(key);
        this.supplier = supplier;
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        return supplier.get().keySet();
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        return supplier.get().get(choice);
    }
}
