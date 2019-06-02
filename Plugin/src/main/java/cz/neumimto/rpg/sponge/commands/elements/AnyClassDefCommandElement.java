package cz.neumimto.rpg.sponge.commands.elements;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.PatternMatchingCommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeumimTo on 5.11.2017.
 */
public class AnyClassDefCommandElement extends PatternMatchingCommandElement {

    public AnyClassDefCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Iterable<String> getChoices(CommandSource source) {
        List<String> suggestions = new ArrayList<>();
        for (ClassDefinition classDefinition : NtRpgPlugin.GlobalScope.classService.getClassDefinitions()) {
            if (classDefinition.getSkillTree() == SkillTree.Default) {
                continue;
            }
            if (source.hasPermission("ntrpg.class." + classDefinition.getName().toLowerCase())) {
                suggestions.add(classDefinition.getName());
            }
        }
        return suggestions;
    }

    @Override
    protected Object getValue(String choice) throws IllegalArgumentException {
        return NtRpgPlugin.GlobalScope.classService.getClassDefinitionByName(choice);
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("<class>");
    }

}