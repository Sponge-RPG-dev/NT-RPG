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

import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillContext;

public class PermissionSkill extends AbstractSkill {

	@Override
	public void onPreUse(IActiveCharacter character, SkillContext skillContext) {
		skillContext.result(SkillResult.CANCELLED);
	}

	@Override
	public void onCharacterInit(IActiveCharacter c, int level) {
		super.onCharacterInit(c, level);

	}

	@Override
	public void skillLearn(IActiveCharacter IActiveCharacter) {
		super.skillLearn(IActiveCharacter);
	}

	@Override
	public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {
		super.skillUpgrade(IActiveCharacter, level);
	}

	@Override
	public void skillRefund(IActiveCharacter IActiveCharacter) {
		super.skillRefund(IActiveCharacter);
	}

}
