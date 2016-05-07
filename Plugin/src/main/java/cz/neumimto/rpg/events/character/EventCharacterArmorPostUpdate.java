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

import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;

import java.util.Set;

/**
 * Created by NeumimTo on 25.7.2015.
 */
public class EventCharacterArmorPostUpdate implements Event {
    IActiveCharacter character;
    Set<ItemType> armor;
    private Cause cause = null;

    public EventCharacterArmorPostUpdate(IActiveCharacter character, Set<ItemType> allowedArmor) {
        this.character = character;
        this.armor = allowedArmor;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public Set<ItemType> getArmor() {
        return armor;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    public void setCause(Cause cause) {
        this.cause = cause;
    }
}
