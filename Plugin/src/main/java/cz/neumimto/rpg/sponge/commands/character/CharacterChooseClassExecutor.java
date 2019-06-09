package cz.neumimto.rpg.sponge.commands.character;

import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.utils.ActionResult;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CharacterChooseClassExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ClassDefinition configClass = args.<ClassDefinition>getOne("class").get();

        LocalizationService localizationService = Rpg.get().getLocalizationService();
        if (!src.hasPermission("ntrpg.class." + configClass.getName().toLowerCase())) {
            src.sendMessage(TextHelper.parse(localizationService.translate(LocalizationKeys.NO_PERMISSIONS)));
            return CommandResult.empty();
        }
        if (!(src instanceof Player)) {
            throw new IllegalStateException("Cannot be run as a console");
        }
        Player player = (Player) src;
        ISpongeCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player.getUniqueId());
        if (character.isStub()) {
            src.sendMessage(TextHelper.parse(localizationService.translate(LocalizationKeys.CHARACTER_IS_REQUIRED)));
            return CommandResult.empty();
        }
        ActionResult result = NtRpgPlugin.GlobalScope.characterService.canGainClass(character, configClass);
        if (result.isOk()) {
            NtRpgPlugin.GlobalScope.characterService.addNewClass(character, configClass);
        } else {
            src.sendMessage(Text.of(result.getErrorMesage()));
        }
        return CommandResult.success();
    }
}
