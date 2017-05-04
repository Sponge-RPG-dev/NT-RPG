/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.players.parties;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.events.party.PartyInviteEvent;
import cz.neumimto.rpg.events.party.PartyJoinEvent;
import cz.neumimto.rpg.events.party.PartyLeaveEvent;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
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
