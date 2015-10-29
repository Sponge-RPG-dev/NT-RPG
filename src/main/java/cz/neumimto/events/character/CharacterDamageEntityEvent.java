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

package cz.neumimto.events.character;

import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by NeumimTo on 13.3.2015.
 */
public class CharacterDamageEntityEvent extends CharacterEvent {
    private Player damaged;
    private double damage;

    public CharacterDamageEntityEvent(IActiveCharacter IActiveCharacter, Player damaged, double amount) {
        super(IActiveCharacter);
        this.damaged = damaged;
        this.damage = amount;
    }

    public Player getDamaged() {
        return damaged;
    }

    public void setDamaged(Player damaged) {
        this.damaged = damaged;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
