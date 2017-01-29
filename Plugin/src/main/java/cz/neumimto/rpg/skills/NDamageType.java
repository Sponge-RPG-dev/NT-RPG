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

package cz.neumimto.rpg.skills;

import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

public class NDamageType {
    public static DamageType LIGHTNING = new DamageType() {
        @Override
        public String getId() {
            return "ntrpg:lightning";
        }

        @Override
        public String getName() {
            return "lightning";
        }
    };
    public static DamageType ICE = new DamageType() {
        @Override
        public String getId() {
            return "ntrpg:ice";
        }

        @Override
        public String getName() {
            return "ice";
        }
    };
    public static DamageType DAMAGE_CHECK = new DamageType() {
        @Override
        public String getId() {
            return "ntrpg:damagecheck";
        }

        @Override
        public String getName() {
            return "damagecheck";
        }
    };
    public static DamageType PHYSICAL = DamageTypes.ATTACK;
    public static DamageType MAGICAL = DamageTypes.MAGIC;
}
