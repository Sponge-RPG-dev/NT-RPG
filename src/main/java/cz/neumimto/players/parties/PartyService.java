package cz.neumimto.players.parties;

import cz.neumimto.configuration.Localization;
import cz.neumimto.events.PartyInviteEvent;
import cz.neumimto.events.PartyJoinEvent;
import cz.neumimto.events.PartyLeaveEvent;
import cz.neumimto.gui.Gui;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.Game;

/**
 * Created by NeumimTo on 2.9.2015.
 */
@Singleton
public class PartyService {

    @Inject
    private Game game;

    public void createNewParty(IActiveCharacter leader) {
        Party party = new Party(leader);
        leader.setParty(party);
    }

    public void kickCharacterFromParty(Party party, IActiveCharacter kicked) {
        if (party.getPlayers().contains(kicked)) {
            PartyLeaveEvent event = new PartyLeaveEvent(party, kicked);
            game.getEventManager().post(event);
            if (event.isCancelled()) {
                return;
            }
            event.getParty().removePlayer(event.getLeaver());
            if (event.getParty().getInvites().contains(kicked.getPlayer().getUniqueId()))
                event.getParty().getInvites().remove(kicked.getPlayer().getUniqueId());
            event.getLeaver().setParty(null);
        }
    }

    public void sendPartyInvite(Party party, IActiveCharacter tcharacter) {
        party.getInvites().add(tcharacter.getPlayer().getUniqueId());
        PartyInviteEvent event = new PartyInviteEvent(party, tcharacter);
        game.getEventManager().post(event);
        if (event.isCancelled())
            return;
        event.getCharacter().setPendingPartyInvite(event.getParty());
    }

    public void addToParty(Party party, IActiveCharacter character) {
        if (character.isStub())
            return;
        if (character.hasParty()) {
            Gui.sendMessage(character, Localization.ALREADY_IN_PARTY);
            return;
        }
        if (party.getInvites().contains(character.getPlayer().getUniqueId()))
            party.getInvites().remove(character.getPlayer().getUniqueId());
        String msg = Localization.PARTY_MSG_ON_PLAYER_JOIN.replaceAll("%1", character.getPlayer().getName());
        PartyJoinEvent event = new PartyJoinEvent(character, party);
        if (event.isCancelled())
            return;
        party.getPlayers().stream().forEach(i -> Gui.sendMessage(character, msg));
        party.addPlayer(character);
        character.setParty(party);
        Gui.sendMessage(character, Localization.PLAYER_MSG_ON_JOIN_PARTY);
    }
}
