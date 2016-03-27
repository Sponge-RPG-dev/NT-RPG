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

package cz.neumimto.rpg.events.skills;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.events.CancellableEvent;

/**
 * Created by NeumimTo on 1.8.2015.
 */
public class SkillPrepareEvent extends CancellableEvent {
    private IActiveCharacter character;
    private float requiredHp;
    private float requiredMana;

    public SkillPrepareEvent(IActiveCharacter character, float requiredHp, float requiredMana) {
        this.character = character;
        this.requiredHp = requiredHp;

        this.requiredMana = requiredMana;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    public float getRequiredHp() {
        return requiredHp;
    }

    public void setRequiredHp(float requiredHp) {
        this.requiredHp = requiredHp;
    }

    public float getRequiredMana() {
        return requiredMana;
    }

    public void setRequiredMana(float requiredMana) {
        this.requiredMana = requiredMana;
    }
}
