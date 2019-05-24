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

package cz.neumimto.rpg.sponge.skills.types;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.skills.types.ITargeted;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.skill.SkillTargetAttemptEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.entity.living.Living;

public abstract class Targeted extends ActiveSkill implements ITargeted {

	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.RANGE, 10, 10);
	}

	@Override
	public void cast(IActiveCharacter caster, PlayerSkillContext info, SkillContext skillContext) {
		int range = skillContext.getIntNodeValue(SkillNodes.RANGE);
		Living l = Utils.getTargetedEntity(caster, range);
		if (l == null) {
			if (getDamageType() == null && !getSkillTypes().contains(SkillType.CANNOT_BE_SELF_CASTED)) {
				l = caster.getEntity();
			} else {
				skillContext.next(caster, info, SkillResult.NO_TARGET); //dont chain
				return;
			}
		}
		if (getDamageType() != null && !Utils.canDamage(caster, l)) {
			skillContext.next(caster, info, SkillResult.CANCELLED); //dont chain
			return;
		}
		IEntity target = NtRpgPlugin.GlobalScope.entityService.get(l);

		SkillTargetAttemptEvent event = new SkillTargetAttemptEvent(caster, target, this);

		if (Rpg.get().postEvent(event)) {
			//todo https://github.com/Sponge-RPG-dev/NT-RPG/issues/111
			skillContext.next((IActiveCharacter) event.getCaster(), info, SkillResult.CANCELLED); //dont chain
			return;
		}
		castOn(event.getTarget(), (IActiveCharacter) event.getCaster(), info, skillContext);
	}
}
