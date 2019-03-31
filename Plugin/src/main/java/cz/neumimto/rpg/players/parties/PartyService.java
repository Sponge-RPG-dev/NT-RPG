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
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.events.party.*;
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
		PartyCreateEvent event = new PartyCreateEvent(leader);
		if (!game.getEventManager().post(event)) {
			Party party = new Party(leader);
			leader.setParty(party);
		}
	}

	public void kickCharacterFromParty(Party party, IActiveCharacter kicked) {
		if (party.getPlayers().contains(kicked)) {
			PartyLeaveEvent event = new PartyLeaveEvent(kicked, party);
			if (game.getEventManager().post(event)) return;

			event.getParty().removePlayer(event.getTarget());
			Player player = kicked.getPlayer();
			event.getParty().getInvites().remove(player.getUniqueId());
			event.getTarget().setParty(null);
		}
	}

	public void sendPartyInvite(Party party, IActiveCharacter character) {
		PartyInviteEvent event = new PartyInviteEvent(character, party);
		if (game.getEventManager().post(event)) return;

		party.sendPartyMessage(Localizations.PLAYER_INVITED_TO_PARTY_PARTY_MSG.toText(Arg.arg("player", character.getPlayer().getName())));
		character.getPlayer().sendMessage(Localizations.PLAYER_INVITED_TO_PARTY.toText(Arg.arg("player", character.getPlayer().getName())));
		party.getInvites().add(character.getPlayer().getUniqueId());
		event.getTarget().setPendingPartyInvite(event.getParty());
	}

	public void addToParty(Party party, IActiveCharacter character) {
		if (character.isStub()) {
			return;
		}
		if (character.hasParty()) {
			Gui.sendMessage(character, Localizations.ALREADY_IN_PARTY, Arg.EMPTY);
			return;
		}
		PartyJoinEvent event = new PartyJoinEvent(character, party);
		if (game.getEventManager().post(event)) return;

		Player player = character.getPlayer();
		party.getInvites().remove(player.getUniqueId());
		Text msg = Localizations.PARTY_MSG_ON_PLAYER_JOIN.toText(Arg.arg("player", player.getName()));
		player.sendMessage(Localizations.PLAYER_MSG_ON_JOIN_PARTY.toText());
		party.sendPartyMessage(msg);
		party.addPlayer(character);
		character.setParty(party);
	}
}
