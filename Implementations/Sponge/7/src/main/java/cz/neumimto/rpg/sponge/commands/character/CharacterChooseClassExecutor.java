package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CharacterChooseClassExecutor implements CommandExecutor {

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Inject
    private SpongeCharacterService characterServise;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ClassDefinition configClass = args.<ClassDefinition>getOne("class").get();

        if (!(src instanceof Player)) {
            throw new IllegalStateException("Cannot be run as a console");
        }
        IActiveCharacter character = characterServise.getCharacter((Player) src);

        boolean b = characterCommandFacade.commandChooseClass(character, configClass);
        return b ? CommandResult.success() : CommandResult.empty();
    }
}
