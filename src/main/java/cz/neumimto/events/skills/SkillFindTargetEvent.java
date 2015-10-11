/*  Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
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
 */
package cz.neumimto.events.skills;

import cz.neumimto.events.CancellableEvent;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;
import org.spongepowered.api.entity.living.Living;

public class SkillFindTargetEvent extends CancellableEvent {
    private IActiveCharacter character;
    private Living target;
    private ISkill skill;

    public SkillFindTargetEvent(IActiveCharacter character, Living target, ISkill targetted) {
        this.character = character;
        this.target = target;
        this.skill = targetted;
    }

    public IActiveCharacter getCharacter() {
        return character;
    }

    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    public Living getTarget() {
        return target;
    }

    public void setTarget(Living target) {
        this.target = target;
    }

    public ISkill getSkill() {
        return skill;
    }

    public void setSkill(ISkill skill) {
        this.skill = skill;
    }
}
