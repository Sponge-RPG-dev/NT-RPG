package cz.neumimto.rpg.sponge.commands.character;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class CharacterShowClassesExecutor implements CommandExecutor {

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private ClassService classService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> classTypeOptional = args.getOne("type");
        if (classTypeOptional.isPresent()) {
            String type = classTypeOptional.get();
            if (src instanceof Player) {
                IActiveCharacter character = characterService.getCharacter((Player) src);
                Gui.sendClassesByType(character, type);
            } else {
                String classes = classService.getClassDefinitions().stream()
                        .filter(a -> a.getClassType().equalsIgnoreCase(type))
                        .map(ClassDefinition::getName)
                        .collect(Collectors.joining(" "));
                src.sendMessage(Text.of("Classes in " + classTypeOptional.get() + " : " + classes));
            }
        } else {
            if (src instanceof Player) {
                IActiveCharacter character = characterService.getCharacter((Player) src);
                Gui.sendClassTypes(character);
            } else {
                src.sendMessage(Text.of("ClassTypes : " + String.join(", ", SpongeRpgPlugin.pluginConfig.CLASS_TYPES.keySet())));
            }
        }
        return CommandResult.success();
    }
}
