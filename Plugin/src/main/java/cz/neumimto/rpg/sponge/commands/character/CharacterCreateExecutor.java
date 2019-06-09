package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.common.persistance.model.JPACharacterBase;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.configuration.CommandLocalization;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterServise;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.util.concurrent.CompletableFuture;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

public class CharacterCreateExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String a = args.<String>getOne("name").get();
        CompletableFuture.runAsync(() -> {
            Player player = (Player) src;
            SpongeCharacterServise characterService = NtRpgPlugin.GlobalScope.characterService;
            int i = characterService.canCreateNewCharacter(player.getUniqueId(), a);
            LocalizationService localizationService = Rpg.get().getLocalizationService();
            if (i == 1) {
                src.sendMessage(TextHelper.parse(localizationService.translate(LocalizationKeys.REACHED_CHARACTER_LIMIT)));
            } else if (i == 2) {
                src.sendMessage(TextHelper.parse(localizationService.translate(LocalizationKeys.CHARACTER_EXISTS)));
            } else if (i == 0) {
                JPACharacterBase characterBase = new JPACharacterBase();
                characterBase.setUuid(player.getUniqueId());
                characterBase.setName(a);
                characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);
                characterBase.setAttributePoints(pluginConfig.ATTRIBUTEPOINTS_ON_START);

                characterService.createAndUpdate(characterBase);

                src.sendMessage(TextHelper.parse(CommandLocalization.CHARACTER_CREATED.replaceAll("%1", characterBase.getName())));
                Gui.sendListOfCharacters(characterService.getCharacter(player.getUniqueId()), characterBase);
            }
        }, NtRpgPlugin.asyncExecutor);
        return CommandResult.success();
    }
}
