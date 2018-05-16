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

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.events.CancellableEvent;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 7.8.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public class SkillHealEvent extends CancellableEvent {
	private IEntity character;
	private double amount;
	private IEffectSourceProvider skill;

	public SkillHealEvent(IEntity character, double amount, IEffectSourceProvider skill) {
		this.character = character;
		this.amount = amount;
		this.skill = skill;
	}

	public IEntity getEntity() {
		return character;
	}

	public void setEntity(IEntity character) {
		this.character = character;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public IEffectSourceProvider getSkill() {
		return skill;
	}

	public void setSkill(IEffectSourceProvider skill) {
		this.skill = skill;
	}
}
