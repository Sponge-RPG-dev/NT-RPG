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
import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.events.party.PartyInviteEvent;
import cz.neumimto.rpg.events.party.PartyJoinEvent;
import cz.neumimto.rpg.events.party.PartyLeaveEvent;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

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
			Player player = kicked.getPlayer();
			event.getParty().getInvites().remove(player.getUniqueId());
			event.getLeaver().setParty(null);
		}
	}

	public void sendPartyInvite(Party party, IActiveCharacter tcharacter) {
		party.sendPartyMessage(TextHelper.parse(Localizations.PLAYER_INVITED_TO_PARTY_PARTY_MSG, Arg.arg("player", tcharacter.getPlayer().getName())));
		tcharacter.getPlayer().sendMessage(TextHelper.parse(Localizations.PLAYER_INVITED_TO_PARTY, Arg.arg("player", tcharacter.getPlayer().getName())));
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
			Gui.sendMessage(character, Localizations.ALREADY_IN_PARTY);
			return;
		}
		Player player = character.getPlayer();
		party.getInvites().remove(player.getUniqueId());
		PartyJoinEvent event = new PartyJoinEvent(character, party);
		if (event.isCancelled())
			return;
		Text msg = TextHelper.parse(Localizations.PARTY_MSG_ON_PLAYER_JOIN, Arg.arg("player", player.getName()));
		player.sendMessage(TextHelper.parse(Localizations.PLAYER_MSG_ON_JOIN_PARTY));
		party.sendPartyMessage(msg);
		party.addPlayer(character);
		character.setParty(party);

	}
}
