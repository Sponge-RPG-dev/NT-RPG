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

package cz.neumimto.rpg.events.character;

import cz.neumimto.rpg.IRpgElement;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 9.8.2015.
 */
public class CharacterManaRegainEvent extends AbstractCharacterCancellableEvent {

	private final IRpgElement source;
	private double amount;

	public CharacterManaRegainEvent(IActiveCharacter character, double amount, IRpgElement source) {
		super(character);
		this.amount = amount;
		this.source = source;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public IRpgElement getSource() {
		return source;
	}

}
