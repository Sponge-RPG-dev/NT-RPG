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

package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.events.skills.SkillFindTargetEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ITargetted;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.utils.SkillModifier;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.living.Living;

public abstract class Targetted extends ActiveSkill implements ITargetted {


	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.RANGE, 10, 10);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		int range = (int) info.getSkillData().getSkillSettings().getLevelNodeValue(SkillNodes.RANGE, info.getTotalLevel());
		Living l = getTargettedEntity(character, range);
		if (l == null) {
			if (getDamageType() == null && !getSkillTypes().contains(SkillType.CANNOT_BE_SELF_CASTED)) {
				l = character.getEntity();
			} else {
				return SkillResult.NO_TARGET;
			}
		}
		if (getDamageType() != null && !Utils.canDamage(character, l)) {
			return SkillResult.CANCELLED;
		}
		SkillFindTargetEvent event = new SkillFindTargetEvent(character, l, this);
		game.getEventManager().post(event);
		if (event.isCancelled()) {
			return SkillResult.CANCELLED;
		}
		return castOn(event.getTarget(), event.getCharacter(), info);
	}


	public Living getTargettedEntity(IActiveCharacter character, int range) {
		return Utils.getTargettedEntity(character, range);
	}
}
