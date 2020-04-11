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

package cz.neumimto.rpg.api.skills.types;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillExecutionType;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

public class PermissionSkill extends AbstractSkill {

    @Override
    public void onPreUse(IActiveCharacter character, SkillContext skillContext) {
        skillContext.result(SkillResult.CANCELLED);
    }

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.PASSIVE;
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(IActiveCharacter, context);
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(IActiveCharacter, level, context);
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(IActiveCharacter, context);
    }

}
