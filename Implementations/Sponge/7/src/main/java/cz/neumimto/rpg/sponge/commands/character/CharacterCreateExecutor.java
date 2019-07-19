package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.common.commands.CharacterCommandFacade;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class CharacterCreateExecutor implements CommandExecutor {

    @Inject
    private CharacterCommandFacade characterCommandFacade;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String name = args.<String>getOne("name").get();
        UUID uuid = ((Player)src).getUniqueId();

        characterCommandFacade.commandCreateCharacter(uuid, name, actionResult -> {
            src.sendMessage(TextHelper.parse(actionResult.getMessage()));
        });
        return CommandResult.success();
    }
}
