package cz.neumimto.rpg.commands;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * Created by NeumimTo on 5.11.2017.
 */
public class AnyClassDefCommandElement extends CommandElement {

	private final boolean validate;

	public AnyClassDefCommandElement(@Nullable Text key) {
		super(key);
		validate = true;
	}

	public AnyClassDefCommandElement(@Nullable Text key, boolean validate) {
		super(key);
		this.validate = validate;
	}

	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String clazz = args.next();
		ClassDefinition configClass = NtRpgPlugin.GlobalScope.classService.getClassDefinitionByName(clazz);
		if (configClass == null) {
			throw args.createError(Localizations.UNKNOWN_CLASS.toText(Arg.arg("class", clazz)));
		}
		IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) source);

		if (validate && pluginConfig.RESPECT_CLASS_SELECTION_ORDER) {
			Set<String> classTypes = pluginConfig.CLASS_TYPES.keySet();

			Iterator<String> ctype = classTypes.iterator();
			String first = classTypes.iterator().next();

			while (ctype.hasNext()) {
				String classType = ctype.next();
				if (first.equalsIgnoreCase(classType) && first.equalsIgnoreCase(configClass.getClassType())) {
					break;
				}
				PlayerClassData classByType = character.getClassByType(classType);
				if (classByType == null) {
					throw args.createError(Text.of("Class type of " + classType + " not selected."));
				}
				ClassDefinition classDefinition = classByType.getClassDefinition();
				if (!classDefinition.getClassDependencyGraph().isValidFor(character.getClasses()
						.values().stream().map(PlayerClassData::getClassDefinition).collect(Collectors.toSet()))) {
					throw args.createError(Localizations.MISSING_CLASS_DEPENDENCIES.toText());
				}
				break;

			}
		}
		if (!source.hasPermission("ntrpg.class." + configClass.getName().toLowerCase())) {
			throw args.createError(TextHelper.parse("&CNo permission ntrpg.class.%class%",
					Arg.arg("class", configClass.getName().toLowerCase())));
		}
		return configClass;
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		if (validate) {
			//todo
			return NtRpgPlugin.GlobalScope.classService.getClassDefinitions().stream()
					.map(ClassDefinition::getName)
					.filter(a -> src.hasPermission("ntrpg.class." + a.toLowerCase()))
					.collect(Collectors.toList());
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public Text getUsage(CommandSource src) {
		return Text.of("<class>");
	}

}