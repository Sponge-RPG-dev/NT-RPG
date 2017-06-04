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

package cz.neumimto.rpg.players.properties;

/**
 * Created by NeumimTo on 30.12.2014.
 */
@PropertyContainer
public class DefaultProperties {

    @Property(name = "max_health")
    public static int max_health;

    @Property(name = "max_mana")
    public static int max_mana;

    @Property(name = "mana")
    public static int mana;

    @Property
    public static int reserved_mana;

    @Property
    public static int reserved_health;

    @Property(name = "reserved_mana_mult", default_ = 1)
    public static int reserved_mana_multiplier;

    @Property(name = "reserved_health_mult", default_ = 1)
    public static int reserved_health_multiplier;

    @Property(default_ = (float) PropertyService.WALKING_SPEED, name = "walk_speed")
    public static int walk_speed;

    @Property(name = "experience_mult", default_ = 1)
    public static int experiences_mult;

    @Property(name = "health_regen", default_ = 1)
    public static int health_regen;

    @Property(name = "mana_regen", default_ = 1)
    public static int mana_regen;

    @Property(name = "health_cost_reduce", default_ = 1)
    public static int health_cost_reduce;

    @Property(name = "mana_cost_reduce", default_ = 1)
    public static int mana_cost_reduce;

    @Property(name = "cooldown_reduce", default_ = 1)
    public static int cooldown_reduce;

    @Property(name = "diamond_sword_bonus_damage", default_ = 0)
    public static int diamond_sword_bonus_damage;

    @Property(name = "golden_sword_bonus_damage", default_ = 0)
    public static int golden_sword_bonus_damage;

    @Property(name = "iron_sword_bonus_damage", default_ = 0)
    public static int iron_sword_bonus_damage;

    @Property(name = "wooden_sword_bonus_damage")
    public static int wooden_sword_bonus_damage;

    @Property(name = "diamond_axe_bonus_damage")
    public static int diamond_axe_bonus_damage;

    @Property(name = "golden_axe_bonus_damage")
    public static int golden_axe_bonus_damage;

    @Property(name = "iron_axe_bonus_damage")
    public static int iron_axe_bonus_damage;

    @Property(name = "wooden_axe_bonus_damage")
    public static int wooden_axe_bonus_damage;

    @Property(name = "diamond_pickaxe_bonus_damage")
    public static int diamond_pickaxe_bonus_damage;

    @Property(name = "golden_pickaxe_bonus_damage")
    public static int golden_pickaxe_bonus_damage;

    @Property(name = "iron_pickaxe_bonus_damage")
    public static int iron_pickaxe_bonus_damage;

    @Property(name = "wooden_pickaxe_bonus_damage")
    public static int wooden_pickaxe_bonus_damage;

    @Property(name = "diamond_hoe_bonus_damage")
    public static int diamond_hoe_bonus_damage;

    @Property(name = "golden_hoe_bonus_damage")
    public static int golden_hoe_bonus_damage;

    @Property(name = "iron_hoe_bonus_damage")
    public static int iron_hoe_bonus_damage;

    @Property(name = "wooden_hoe_bonus_damage")
    public static int wooden_hoe_bonus_damage;

    @Property(name = "swords_damage_mult", default_ = 1)
    public static int swords_damage_mult;

    @Property(name = "axes_damage_mult", default_ = 1)
    public static int axes_damage_mult;

    @Property(name = "pickaxes_damage_mult", default_ = 1)
    public static int pickaxes_damage_mult;

    @Property(name = "hoes_damage_mult", default_ = 1)
    public static int hoes_damage_mult;

    @Property(name = "bows_meele_damage_mult", default_ = 1)
    public static int bows_meele_damage_mult;

    @Property(name = "bow_meele_bonus_damage", default_ = 0)
    public static int bow_meele_bonus_damage;

    @Property(name = "fire_damage_bonus_mult", default_ = 1)
    public static int fire_damage_bonus_mult;

    @Property(name = "fire_damage_protection_mult", default_ = 1)
    public static int fire_damage_protection_mult;

    @Property(name = "physical_damage_protection_mult", default_ = 1)
    public static int physical_damage_protection_mult;

    @Property(name = "lightning_damage_bonus_mult", default_ = 1)
    public static int lightning_damage_bonus_mult;

    @Property(name = "lightning_damage_protection_mult", default_ = 1)
    public static int lightning_damage_protection_mult;

    @Property(name = "magic_damage_bonus_mult", default_ = 1)
    public static int magic_damage_bonus_mult;

    @Property(name = "magic_damage_protection_mult", default_ = 1)
    public static int magic_damage_protection_mult;

    @Property(name = "weapon_damage_bonus")
    public static int weapon_damage_bonus;

    @Property(name = "mana_regen_mult", default_ = 0)
    public static int mana_regen_mult;

    @Property(name = "ice_damage_protection_mult", default_ = 1)
    public static int ice_damage_protection_mult;

    @Property(name = "physical_damage_bonus_mult", default_ = 1)
    public static int physical_damage_bonus_mult;

    @Property(name = "ice_damage_bonus_mult", default_ = 1)
    public static int ice_damage_bonus_mult;

    @Property(name = "staffs_damage_mult", default_ = 1)
    public static int staffs_damage_mult;

    @Property
    public static int all_skills_bonus;
}
