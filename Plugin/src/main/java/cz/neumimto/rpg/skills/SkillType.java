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

package cz.neumimto.rpg.skills;

import org.spongepowered.api.util.Tristate;

import static org.spongepowered.api.util.Tristate.*;

/**
 * Created by NeumimTo on 26.7.2015.
 */
public enum SkillType implements ISkillType {
	PHYSICAL(UNDEFINED),
	HEALING(FALSE),
	SUMMON(UNDEFINED),
	PROJECTILE(UNDEFINED),
	CAN_CAST_WHILE_SILENCED(FALSE),
	UTILITY(UNDEFINED),
	HEALTH_DRAIN(TRUE),
	AURA(UNDEFINED),
	CURSE(TRUE),
	AOE(UNDEFINED),
	DECREASED_RESISTANCE(UNDEFINED),
	ESCAPE(FALSE),
	TELEPORT(UNDEFINED),
	STEALTH(FALSE),
	MOVEMENT(UNDEFINED),
	DISEASE(TRUE),
	FIRE(UNDEFINED),
	ELEMENTAL(UNDEFINED),
	LIGHTNING(UNDEFINED),
	ICE(UNDEFINED),
	DRAIN(TRUE),
	CANNOT_BE_SELF_CASTED(UNDEFINED),
	PROTECTION(TRUE),
	/**
	 * Do not use this one in custom skills
	 */
	PATH(UNDEFINED);

    private Tristate negative;

	SkillType(Tristate negative) {
		this.negative = negative;
	}

	@Override
	public Tristate isNegative() {
		return negative;
	}
}
