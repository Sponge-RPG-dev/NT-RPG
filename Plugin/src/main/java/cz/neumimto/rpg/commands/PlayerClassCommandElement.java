package cz.neumimto.rpg.commands;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.Race;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * Created by NeumimTo on 5.11.2017.
 */
public class PlayerClassCommandElement extends CommandElement {

	private final boolean validate;

	public PlayerClassCommandElement(@Nullable Text key) {
		super(key);
		validate = true;
	}

	public PlayerClassCommandElement(@Nullable Text key, boolean validate) {
		super(key);
		this.validate = validate;
	}

	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String clazz = args.next();
		ConfigClass configClass = NtRpgPlugin.GlobalScope.groupService.getNClass(clazz);
		if (configClass == null) {
			throw args.createError(Localizations.UNKNOWN_CLASS.toText(Arg.arg("class", clazz)));
		}
		IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) source);

		if (validate && PluginConfig.VALIDATE_RACE_DURING_CLASS_SELECTION) {
			Race race = character.getRace();
			if (race == Race.Default) {
				throw args.createError(Localizations.RACE_NOT_SELECTED.toText());
			}
			if (!race.getAllowedClasses().contains(configClass)) {
				throw args.createError(Localizations.RACE_CANNOT_BECOME_CLASS.toText(
						Arg.arg("race", race.getName()).with("class", configClass.getName())));
			}
		}
		if (!source.hasPermission("ntrpg.groups." + configClass.getName().toLowerCase())) {
			throw args.createError(TextHelper.parse("&CNo permission ntrpg.groups.%class%",
					Arg.arg("class", configClass.getName().toLowerCase())));
		}
		return configClass;
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		if (PluginConfig.VALIDATE_RACE_DURING_CLASS_SELECTION) {
			IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
			Race race = character.getRace();
			if (race == Race.Default) {
				return Collections.emptyList();
			}
			return race.getAllowedClasses().stream()
					.map(ConfigClass::getName)
					.filter(a -> src.hasPermission("ntrpg.groups." + a.toLowerCase()))
					.collect(Collectors.toList());
		}
		return NtRpgPlugin.GlobalScope.groupService.getClasses().stream()
				.map(ConfigClass::getName)
				.filter(a -> src.hasPermission("ntrpg.groups." + a.toLowerCase()))
				.collect(Collectors.toList());
	}

	@Override
	public Text getUsage(CommandSource src) {
		return Text.of("<class>");
	}

}