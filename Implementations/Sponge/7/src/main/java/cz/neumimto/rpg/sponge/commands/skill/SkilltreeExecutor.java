package cz.neumimto.rpg.sponge.commands.skill;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

@Singleton
public class SkilltreeExecutor implements CommandExecutor {
    
    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private LocalizationService localizationService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

    }
}
