package cz.neumimto.rpg.sponge.commands.elements;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 16.11.2017.
 */
public class CharacterAttributeCommandElement extends PatternMatchingCommandElement {

    public CharacterAttributeCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        Collection<AttributeConfig> allOf = Rpg.get().getPropertyService().getAttributes().values();
        return allOf.stream().map(AttributeConfig::getId).collect(Collectors.toSet());
    }

    @Override
    protected Object getValue(String choice) {
        return Rpg.get().getPropertyService().getAttributeById(choice);
    }

}
