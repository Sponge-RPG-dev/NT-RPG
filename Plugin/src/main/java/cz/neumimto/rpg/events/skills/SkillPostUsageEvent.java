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

import cz.neumimto.rpg.events.CancellableEvent;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 7.8.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public class SkillPostUsageEvent extends CancellableEvent {
	private IActiveCharacter character;
	private double hpcost;
	private double manacost;
	private float cooldown;

	public SkillPostUsageEvent(IActiveCharacter character, double hpcost, double manacost, float cooldown) {
		this.character = character;
		this.hpcost = hpcost;
		this.manacost = manacost;
		this.cooldown = cooldown;
	}


	public IActiveCharacter getCharacter() {
		return character;
	}

	public void setCharacter(IActiveCharacter character) {
		this.character = character;
	}

	public double getHpcost() {
		return hpcost;
	}

	public void setHpcost(float hpcost) {
		this.hpcost = hpcost;
	}

	public double getManacost() {
		return manacost;
	}

	public void setManacost(float manacost) {
		this.manacost = manacost;
	}

	public float getCooldown() {
		return cooldown;
	}

	public void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}
}
