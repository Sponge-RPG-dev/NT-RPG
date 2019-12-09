package cz.neumimto.rpg.sponge.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterService;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("party|nparty")
public class SpongePartyCommands extends BaseCommand {

    @Inject
    private SpongeCharacterService characterService;

    @Inject
    private SpongePartyService partyService;

    @Inject
    private LocalizationService localizationService;

    @Subcommand("accept")
    public void acceptPartyInviteCommand(Player executor) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        if (character.getPendingPartyInvite() != null) {
            partyService.addToParty(character.getPendingPartyInvite(), character);
        }
    }

    @Subcommand("invite")
    public void inviteToPartyCommand(Player executor, @Flags("target") Player target) {
        partyService.sendPartyInvite(
                characterService.getCharacter(executor).getParty(),
                characterService.getCharacter(target));
    }

    @Subcommand("create")
    public void createPartyCommand(Player executor) {
        IActiveCharacter character = characterService.getCharacter(executor);
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        if (character.isStub()) {
            character.sendMessage(localizationService.translate(LocalizationKeys.CHARACTER_IS_REQUIRED));
            return;
        }
        if (character.hasParty()) {
            character.sendMessage(localizationService.translate(LocalizationKeys.ALREADY_IN_PARTY));
            return;
        }
        Rpg.get().getPartyService().createNewParty(character);
        character.sendMessage(localizationService.translate(LocalizationKeys.PARTY_CREATED));
    }

    @Subcommand("kick")
    public void kickFromPartyCommand(Player executor, @Flags("target") Player target) {
        ISpongeCharacter character = characterService.getCharacter(executor);
        ISpongeCharacter character2 = characterService.getCharacter(target);
        Rpg.get().getPartyService().kickCharacterFromParty(character.getParty(), character2);
    }
}
