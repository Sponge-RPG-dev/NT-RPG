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

package cz.neumimto.rpg.sponge.properties;

import cz.neumimto.rpg.api.properties.Property;
import cz.neumimto.rpg.api.properties.PropertyContainer;

/**
 * Created by NeumimTo on 30.12.2014.
 */
@PropertyContainer
public class SpongeDefaultProperties {


    @Property(name = "physical_damage_protection_mult", default_ = 1)
    public static int physical_damage_protection_mult;

    @Property(name = "magic_damage_protection_mult", default_ = 1)
    public static int magic_damage_protection_mult;

    @Property(name = "fire_damage_protection_mult", default_ = 1)
    public static int fire_damage_protection_mult;

    @Property(name = "lightning_damage_protection_mult", default_ = 1)
    public static int lightning_damage_protection_mult;

    @Property(name = "ice_damage_bonus_mult", default_ = 1)
    public static int ice_damage_bonus_mult;

    @Property(name = "ice_damage_protection_mult", default_ = 1)
    public static int ice_damage_protection_mult;

    @Property(name = "other_projectile_damage_mult", default_ = 1)
    public static int other_projectile_damage_mult;

}
