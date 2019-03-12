package cz.neumimto.rpg.commands.elements;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

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
		String attributeName = args.next();
		ICharacterAttribute attribute = NtRpgPlugin.GlobalScope.propertyService.getAttribute(attributeName);
		if (attribute == null) {
			throw args.createError(Localizations.UNKNOWN_ATTRIBUTE.toText(Arg.arg("attribute", attributeName)));
		}
		return attribute;
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		return new ArrayList<>(NtRpgPlugin.GlobalScope.propertyService.getAttributes().keySet());
	}
}
