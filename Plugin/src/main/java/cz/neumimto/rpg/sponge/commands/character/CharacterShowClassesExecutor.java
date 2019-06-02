package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.stream.Collectors;

public class CharacterShowClassesExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> classTypeOptional = args.getOne("type");
        if (classTypeOptional.isPresent()) {
            String type = classTypeOptional.get();
            if (src instanceof Player) {
                IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
                Gui.sendClassesByType(character, type);
            } else {
                String classes = NtRpgPlugin.GlobalScope.classService.getClassDefinitions().stream()
                        .filter(a -> a.getClassType().equalsIgnoreCase(type))
                        .map(ClassDefinition::getName)
                        .collect(Collectors.joining(" "));
                src.sendMessage(Text.of("Classes in " + classTypeOptional.get() + " : " + classes));
            }
        } else {
            if (src instanceof Player) {
                IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
                Gui.sendClassTypes(character);
            } else {
                src.sendMessage(Text.of("ClassTypes : " + String.join(", ", NtRpgPlugin.pluginConfig.CLASS_TYPES.keySet())));
            }
        }
        return CommandResult.success();
    }
}
