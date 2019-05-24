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

package cz.neumimto.rpg.damage;


import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

/**
 * Created by NeumimTo on 4.8.15.
 */
//todo registry
public enum ProjectileType {
    ARROW,
    EGG,
    SNOWBALL,
    SMALL_FIREBALL,
    LARGE_FIREBALL,
    ENDER_PEARL,
    WITHER_SKULL,
    THROWN_EXP_BOTTLE,
    CUSTOM; //mods

    public static ProjectileType fromEntityType(EntityType type) {
        if (type == EntityTypes.TIPPED_ARROW || type == EntityTypes.SPECTRAL_ARROW) {
            return ARROW;
        }
        if (type == EntityTypes.EGG) {
            return EGG;
        }
        if (type == EntityTypes.SNOWBALL) {
            return SNOWBALL;
        }
        if (type == EntityTypes.SMALL_FIREBALL) {
            return SMALL_FIREBALL;
        }
        if (type == EntityTypes.FIREBALL) {
            return LARGE_FIREBALL;
        }
        if (type == EntityTypes.ENDER_PEARL) {
            return ENDER_PEARL;
        }
        if (type == EntityTypes.WITHER_SKULL) {
            return WITHER_SKULL;
        }
        if (type == EntityTypes.THROWN_EXP_BOTTLE) {
            return THROWN_EXP_BOTTLE;
        }
        return CUSTOM;
    }
}
