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

package cz.neumimto.rpg.sponge.skills;

import org.spongepowered.api.event.cause.entity.damage.DamageType;

public class NDamageType {

    public static final DamageType FIRE = new DamageType() {
        @Override
        public String getId() {
            return "ntrpg:fire";
        }

        @Override
        public String getName() {
            return "fire";
        }

        @Override
        public String toString() {
            return "Fire";
        }
    };

    public static final DamageType LIGHTNING = new DamageType() {
        @Override
        public String getId() {
            return "ntrpg:lightning";
        }

        @Override
        public String getName() {
            return "lightning";
        }

        @Override
        public String toString() {
            return "Lightning";
        }
    };

    public static final DamageType ICE = new DamageType() {
        @Override
        public String getId() {
            return "ntrpg:ice";
        }

        @Override
        public String getName() {
            return "ice";
        }

        @Override
        public String toString() {
            return "Ice";
        }
    };

}
