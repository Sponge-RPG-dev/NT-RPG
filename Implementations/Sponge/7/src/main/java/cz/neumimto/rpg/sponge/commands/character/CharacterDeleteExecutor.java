package cz.neumimto.rpg.sponge.commands.character;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.concurrent.CompletableFuture;

@Singleton
public class CharacterDeleteExecutor implements CommandExecutor {
    @Inject
    private SpongeCharacterService characterService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String a = args.<String>getOne("name").get();
        Player player = (Player) src;
        IActiveCharacter character = characterService.getCharacter(player);
        if (character.getName().equalsIgnoreCase(a)) {
            characterService.removeCachedCharacter(player.getUniqueId());
            characterService.registerDummyChar(characterService.buildDummyChar(player.getUniqueId()));
        }
        CompletableFuture.runAsync(() -> {
            characterService.markCharacterForRemoval(player.getUniqueId(), a);
            String translated = Rpg.get().getLocalizationService().translate(LocalizationKeys.CHAR_DELETED_FEEDBACK);
            player.sendMessage(TextHelper.parse(translated));
        }, SpongeRpgPlugin.asyncExecutor);
        return CommandResult.success();
    }
}
