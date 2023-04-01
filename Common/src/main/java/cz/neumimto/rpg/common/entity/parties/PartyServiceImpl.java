package cz.neumimto.rpg.common.entity.parties;


import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.parties.PartyService;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.common.events.party.PartyCreateEvent;
import cz.neumimto.rpg.common.events.party.PartyInviteEvent;
import cz.neumimto.rpg.common.events.party.PartyJoinEvent;
import cz.neumimto.rpg.common.events.party.PartyLeaveEvent;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;

import javax.inject.Inject;

/**
 * Created by NeumimTo on 2.9.2015.
 */
public abstract class PartyServiceImpl<T extends ActiveCharacter> implements PartyService<T> {

    @Inject
    private LocalizationService localizationService;

    @Override
    public void createNewParty(T leader) {
        PartyCreateEvent event = Rpg.get().getEventFactory().createEventInstance(PartyCreateEvent.class);
        if (!Rpg.get().postEvent(event)) {
            event.setCharacter(leader);
            event.setParty(createParty(leader));
            leader.setParty(event.getParty());
        }
    }

    protected abstract IParty createParty(T leader);

    @Override
    public boolean kickCharacterFromParty(IParty party, T kicked) {
        if (party.getPlayers().contains(kicked)) {
            PartyLeaveEvent event = Rpg.get().getEventFactory().createEventInstance(PartyLeaveEvent.class);
            event.setCharacter(kicked);
            event.setParty(party);

            if (Rpg.get().postEvent(event)) {
                return false;
            }

            event.getParty().removePlayer(event.getCharacter());
            event.getCharacter().setParty(null);
            event.getParty().getInvites().remove(event.getCharacter().getUUID());
            return true;
        }
        return false;
    }

    @Override
    public void sendPartyInvite(IParty party, T character) {
        PartyInviteEvent event = Rpg.get().getEventFactory().createEventInstance(PartyInviteEvent.class);
        event.setCharacter(character);
        event.setParty(party);

        if (Rpg.get().postEvent(event)) {
            return;
        }

        party.getInvites().add(character.getUUID());
        event.getCharacter().setPendingPartyInvite(event.getParty());

        String msg = localizationService.translate(LocalizationKeys.PLAYER_INVITED_TO_PARTY_PARTY_MSG, Arg.arg("player", character.getName()));
        party.sendPartyMessage(msg);
        msg = localizationService.translate(LocalizationKeys.PLAYER_INVITED_TO_PARTY, Arg.arg("player", character.getName()));
        character.sendMessage(msg);

    }

    @Override
    public void addToParty(IParty party, T character) {
        if (character.isStub()) {
            return;
        }
        if (character.hasParty()) {
            String msg = localizationService.translate(LocalizationKeys.ALREADY_IN_PARTY);
            character.sendMessage(msg);
            return;
        }

        PartyJoinEvent event = Rpg.get().getEventFactory().createEventInstance(PartyJoinEvent.class);
        event.setCharacter(character);
        event.setParty(party);

        if (Rpg.get().postEvent(event)) {
            return;
        }


        party.getInvites().remove(character.getUUID());

        String msg = localizationService.translate(LocalizationKeys.PLAYER_MSG_PARTY_JOINED, Arg.arg("player", character.getName()));
        character.sendMessage(msg);

        msg = localizationService.translate(LocalizationKeys.PARTY_MSG_PLAYER_JOINED, Arg.arg("player", character.getName()));
        party.sendPartyMessage(msg);
        party.addPlayer(character);
        character.setParty(party);
    }
}
