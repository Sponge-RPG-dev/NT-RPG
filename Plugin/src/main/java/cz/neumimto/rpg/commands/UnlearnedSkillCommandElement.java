package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.NtRpgPlugin;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by NeumimTo on 16.11.2017.
 */
public class UnlearnedSkillCommandElement extends CommandElement {
    public UnlearnedSkillCommandElement(Text skill) {
        super(skill);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String next = args.next();
        return NtRpgPlugin.GlobalScope.skillService.getSkillByLocalizedName(next);
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Collections.emptyList();
    }


}
