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

package cz.neumimto.rpg.sponge.utils.math;

import cz.neumimto.rpg.api.utils.rng.XORShiftRnd;

import java.util.UUID;

/**
 * Created by NeumimTo on 23.7.2015.
 */
//todo move this to ntcore
public class UUIDs {

    /*
     * UUID.randomUUID uses secure random, which is slow and we arent
     * using it for crypthography
     * */
    public static UUID random() {
        XORShiftRnd r = new XORShiftRnd();
        return new UUID(r.nextLong(), r.nextLong());
    }
}
