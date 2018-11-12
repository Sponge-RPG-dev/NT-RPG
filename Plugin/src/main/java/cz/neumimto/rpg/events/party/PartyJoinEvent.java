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

package cz.neumimto.rpg.events.party;

import cz.neumimto.rpg.events.CancellableEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.parties.Party;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by ja on 2.9.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public class PartyJoinEvent extends CancellableEvent {

	private final IActiveCharacter character;
	private final Party party;

	public PartyJoinEvent(IActiveCharacter character, Party party) {
		this.character = character;
		this.party = party;
	}

	public IActiveCharacter getCharacter() {
		return character;
	}

	public Party getParty() {
		return party;
	}
}
