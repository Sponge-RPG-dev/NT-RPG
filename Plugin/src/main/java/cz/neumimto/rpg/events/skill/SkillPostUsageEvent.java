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

package cz.neumimto.rpg.events.skill;

import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.entity.AbstractIEntityCancellableEvent;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.mods.SkillContext;

/**
 * Created by NeumimTo on 7.8.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public class SkillPostUsageEvent extends AbstractIEntityCancellableEvent implements SkillEvent {
	private final SkillContext context;

	public SkillPostUsageEvent(IEntity target, SkillContext context) {
		super(target);
		this.context = context;
	}

	public SkillContext getSkillContext() {
		return context;
	}

	@Override
	public ISkill getSkill() {
		return context.getSkill();
	}
}
