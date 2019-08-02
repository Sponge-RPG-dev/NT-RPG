package cz.neumimto.rpg.sponge.commands.elements;

import cz.neumimto.rpg.api.Rpg;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;

/**
 * Created by NeumimTo on 16.11.2017.
 */
public class LearnedSkillCommandElement extends PatternMatchingCommandElement {

    public LearnedSkillCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        return Rpg.get().getCharacterService().getCharacter(((Player) source).getUniqueId()).getSkills().keySet();
    }

    @Override
    protected Object getValue(String choice) {
        return Rpg.get().getSkillService().getSkillByLocalizedName(choice);
    }

}
