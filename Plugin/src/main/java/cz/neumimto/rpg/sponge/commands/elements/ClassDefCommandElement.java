package cz.neumimto.rpg.sponge.commands.elements;

import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 17.11.2017.
 */
public class ClassDefCommandElement extends PatternMatchingCommandElement {

    public ClassDefCommandElement(Text cls) {
        super(cls);
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        return NtRpgPlugin.GlobalScope.classService.getClassDefinitions().stream()
                .filter(a -> source.hasPermission("ntrpg.class." + a.getName().toLowerCase()))
                .map(a -> a.getName())
                .collect(Collectors.toList());
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        return NtRpgPlugin.GlobalScope.classService.getClassDefinitionByName(choice);
    }
}
