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

package cz.neumimto.effects;

/**
 * Created by NeumimTo on 17.2.2015.
 */
public enum EffectSource implements IEffectSource {
    DEFAULT(false),
    RACE(false),
    GUILD(false),
    ITEM(true),
    PASSIVE_SKILL(false),
    TEMP(true);

    private boolean clearOnDeath;

    EffectSource(boolean clearOnDeath) {
        this.clearOnDeath = clearOnDeath;
    }

    public boolean isClearedOnDeath() {
        return clearOnDeath;
    }
}
