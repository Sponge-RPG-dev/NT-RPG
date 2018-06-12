package cz.neumimto.rpg.commands;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeumimTo on 16.11.2017.
 */
public class CharacterAttributeCommandElement extends CommandElement {
    public CharacterAttributeCommandElement(Text attribute) {
        super(attribute);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String next = args.next();
        ICharacterAttribute attribute = NtRpgPlugin.GlobalScope.propertyService.getAttribute(next);
        if (attribute == null) {
            throw args.createError(TextHelper.parse(Localizations.UNKNOWN_ATTRIBUTE, Arg.arg("attribute", next)));
        }
        return attribute;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return new ArrayList<>(NtRpgPlugin.GlobalScope.propertyService.getAttributes().keySet());
    }
}
