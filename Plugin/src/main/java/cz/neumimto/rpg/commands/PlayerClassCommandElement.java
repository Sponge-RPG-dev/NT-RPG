package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.TextHelper;
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

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 5.11.2017.
 */
public class PlayerClassCommandElement extends CommandElement {

    public PlayerClassCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String clazz = args.next();
        ConfigClass configClass = NtRpgPlugin.GlobalScope.groupService.getNClass(clazz);
        if (configClass == null) {
            throw args.createError(TextHelper.parse("&CUnknown class %s", clazz));
        }
        IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) source);

        if (PluginConfig.VALIDATE_RACE_DURING_CLASS_SELECTION) {
            Race race = character.getRace();
            if (race == Race.Default) {
                throw args.createError(TextHelper.parse("&CYou have to select race before class"));
            }
            if (!race.getAllowedClasses().contains(configClass)) {
                throw args.createError(TextHelper.parse("&CRace %s cannot become %s", race.getName(), configClass.getName()));
            }
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
                    .filter(a -> src.hasPermission("ntrpg.classes."+a.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return NtRpgPlugin.GlobalScope.groupService.getClasses().stream()
                .map(ConfigClass::getName)
                .filter(a -> src.hasPermission("ntrpg.classes."+a.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("<class>");
    }

}