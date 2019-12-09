package cz.neumimto.rpg.common.commands;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.localization.LocalizationService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SuppressWarnings("unchecked")
public class PartyCommandFacade {

    @Inject
    private PartyService partyService;

    public void acceptPartyInvite(IActiveCharacter character) {
        if (character.getPendingPartyInvite() != null) {
            partyService.addToParty(character.getPendingPartyInvite(), character);
        }
    }

    public void createParty(IActiveCharacter character) {
        LocalizationService localizationService = Rpg.get().getLocalizationService();
        if (character.isStub()) {
            character.sendMessage(localizationService.translate(LocalizationKeys.CHARACTER_IS_REQUIRED));
            return;
        }
        if (character.hasParty()) {
            character.sendMessage(localizationService.translate(LocalizationKeys.ALREADY_IN_PARTY));
            return;
        }
        partyService.createNewParty(character);
        character.sendMessage(localizationService.translate(LocalizationKeys.PARTY_CREATED));
    }
}
