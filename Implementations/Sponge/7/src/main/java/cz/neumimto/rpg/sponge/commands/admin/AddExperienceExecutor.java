package cz.neumimto.rpg.sponge.commands.admin;

import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.commands.AdminCommandFacade;
import cz.neumimto.rpg.common.commands.CommandProcessingException;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class AddExperienceExecutor implements CommandExecutor {

    @Inject
    private AdminCommandFacade adminCommandFacade;

    @Inject
    private SpongeCharacterService characterService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        Double amount = args.<Double>getOne("amount").get();
        Optional<ClassDefinition> classDefinition = args.getOne("class");
        Optional<String> expSource = args.getOne("source");

        ISpongeCharacter character = characterService.getCharacter(player.getUniqueId());

        try {
            adminCommandFacade.commandAddExperiences(character, amount, classDefinition.orElse(null), expSource.orElse(null));
            return CommandResult.success();
        } catch (CommandProcessingException e) {
            src.sendMessage(Text.of(e.getMessage()));
        }
        return CommandResult.empty();

    }
}
