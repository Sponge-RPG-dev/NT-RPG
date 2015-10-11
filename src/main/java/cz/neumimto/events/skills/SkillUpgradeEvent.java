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

package cz.neumimto.events.skills;

import cz.neumimto.events.CancellableEvent;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public class SkillUpgradeEvent extends CancellableEvent {
    IActiveCharacter character;
    ISkill skill;
    int level;

    public SkillUpgradeEvent(IActiveCharacter character, ISkill skill, int level) {
        this.character = character;
        this.skill = skill;
        this.level = level;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    public ISkill getSkill() {
        return skill;
    }

    public void setSkill(ISkill skill) {
        this.skill = skill;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
