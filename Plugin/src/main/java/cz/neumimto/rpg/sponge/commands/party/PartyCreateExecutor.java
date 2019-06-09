package cz.neumimto.rpg.sponge.commands.party;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class PartyCreateExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter((Player) src);
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        if (character.isStub()) {
            character.sendMessage(localizationService.translate(LocalizationKeys.CHARACTER_IS_REQUIRED));
            return CommandResult.success();
        }
        if (character.hasParty()) {
            character.sendMessage(localizationService.translate(LocalizationKeys.ALREADY_IN_PARTY));
            return CommandResult.success();
        }
        Rpg.get().getPartyService().createNewParty(character);
        character.sendMessage(localizationService.translate(LocalizationKeys.PARTY_CREATED));
        return CommandResult.success();
    }
}
