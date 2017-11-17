package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.players.groups.Race;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 16.11.2017.
 */
public class RaceCommandElement extends CommandElement {
    public RaceCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String race = args.next();
        Race r = NtRpgPlugin.GlobalScope.groupService.getRace(race);
        if (r == null) {
            throw args.createError(TextHelper.parse("&CUnknown race %s", race));
        }
        if (!source.hasPermission("ntrpg.races."+r.getName().toLowerCase())) {
            throw args.createError(TextHelper.parse("&CNo permission ntrpg.races.%s", r.getName().toLowerCase()));
        }
        return r;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return NtRpgPlugin.GlobalScope.groupService.getRaces().stream()
                .map(Race::getName)
                .filter(a -> src.hasPermission("ntrpg.races."+a.toLowerCase()))
                .collect(Collectors.toList());

    }
}
