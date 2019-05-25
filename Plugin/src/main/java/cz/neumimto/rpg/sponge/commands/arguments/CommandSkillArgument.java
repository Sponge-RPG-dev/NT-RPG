package cz.neumimto.rpg.sponge.commands.arguments;

import cz.neumimto.rpg.api.Rpg;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

public class CommandSkillArgument extends PatternMatchingCommandElement {

    public CommandSkillArgument(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        return Rpg.get().getSkillService().getSkills().keySet();
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        return Rpg.get().getSkillService().getById(choice);
    }
}
