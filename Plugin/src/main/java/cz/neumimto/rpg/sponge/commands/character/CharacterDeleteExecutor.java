package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.concurrent.CompletableFuture;

public class CharacterDeleteExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String a = args.<String>getOne("name").get();
        Player player = (Player) src;
        CharacterService characterService = NtRpgPlugin.GlobalScope.characterService;
        IActiveCharacter character = characterService.getCharacter(player);
        if (character.getName().equalsIgnoreCase(a)) {
            characterService.removeCachedCharacter(player.getUniqueId());
            characterService.registerDummyChar(characterService.buildDummyChar(player.getUniqueId()));
        }
        CompletableFuture.runAsync(() -> {
            characterService.markCharacterForRemoval(player.getUniqueId(), a);
            player.sendMessage(Localizations.CHAR_DELETED_FEEDBACK.toText());
        }, NtRpgPlugin.asyncExecutor);
        return CommandResult.success();
    }
}
