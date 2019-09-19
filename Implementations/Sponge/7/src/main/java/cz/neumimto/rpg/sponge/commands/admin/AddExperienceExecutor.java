package cz.neumimto.rpg.sponge.commands.admin;

import cz.neumimto.rpg.common.commands.AdminCommandFacade;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AddExperienceExecutor implements CommandExecutor {

    @Inject
    private AdminCommandFacade adminCommandFacade;

    @Inject
    private SpongeCharacterService characterService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {


    }
}
