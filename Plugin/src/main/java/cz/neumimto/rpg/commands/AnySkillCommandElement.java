package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Created by NeumimTo on 5.11.2017.
 */
public class AnySkillCommandElement extends CommandElement {

    public AnySkillCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String skilllc = args.next().toLowerCase();
        Optional<ISkill> skill = NtRpgPlugin.GlobalScope.skillService.getById(skilllc);
        if (!skill.isPresent()) {
            throw args.createError(TextSerializers.FORMATTING_CODE.deserialize("&CUnknown skill &C\"" + skilllc + "\""));
        }
        return skill.get();
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return new ArrayList<>(NtRpgPlugin.GlobalScope.skillService.getSkills().keySet());
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("<skill_name>");
    }

}
