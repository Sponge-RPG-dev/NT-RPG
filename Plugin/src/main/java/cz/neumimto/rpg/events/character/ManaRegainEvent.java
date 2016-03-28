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

import cz.neumimto.rpg.events.CancellableEvent;
import cz.neumimto.rpg.players.IActiveCharacter;


/**
 * Created by NeumimTo on 9.8.2015.
 */
public class ManaRegainEvent extends CancellableEvent {
    private IActiveCharacter character;
    private double newVal;
    private double amount;

    public ManaRegainEvent(IActiveCharacter character) {
        this.character = character;
    }

    public ManaRegainEvent(IActiveCharacter character, double newVal) {
        this.character = character;
        this.newVal = newVal;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    public double getNewVal() {
        return newVal;
    }

    public void setNewVal(double newVal) {
        this.newVal = newVal;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}
