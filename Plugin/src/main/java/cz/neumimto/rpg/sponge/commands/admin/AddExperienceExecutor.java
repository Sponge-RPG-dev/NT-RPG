package cz.neumimto.rpg.sponge.commands.admin;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.Optional;

public class AddExperienceExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        Double amount = args.<Double>getOne("amount").get();
        Optional<ClassDefinition> classDefinition = args.getOne("class");
        Optional<String> expSource = args.getOne("source");

        IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
        Collection<PlayerClassData> classes = character.getClasses().values();

        if (classDefinition.isPresent()) {
            classes.stream()
                    .filter(PlayerClassData::takesExp)
                    .filter(c -> c.getClassDefinition().getName().equalsIgnoreCase(classDefinition.get().getName()))
                    .forEach(c -> NtRpgPlugin.GlobalScope.characterService.addExperiences(character, amount, c));
        } else if (expSource.isPresent()) {
            NtRpgPlugin.GlobalScope.characterService.addExperiences(character, amount, expSource.get());
        } else {
            src.sendMessage(Text.of("Specify class or experience source!"));
            return CommandResult.empty();
        }
        return CommandResult.success();
    }
}
