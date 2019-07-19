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

package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.api.events.character.EventCharacterArmorPostUpdate;
import cz.neumimto.rpg.api.items.RpgItemType;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Created by NeumimTo on 25.7.2015.
 */
public class SpigotEventCharacterArmorPostUpdate extends AbstractCharacterEvent implements EventCharacterArmorPostUpdate {

    private Set<RpgItemType> armor;

    @Override
    public Set<RpgItemType> getArmor() {
        return armor;
    }

    @Override
    public void setArmor(Set<RpgItemType> armor) {
        this.armor = armor;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
