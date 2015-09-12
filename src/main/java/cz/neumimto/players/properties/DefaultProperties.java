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

package cz.neumimto.players.properties;

/**
 * Created by NeumimTo on 30.12.2014.
 */
@PropertyContainer
public class DefaultProperties {

    @Property(name = "max_health")
    public static short max_health;

    @Property(name = "max_mana")
    public static short max_mana;

    @Property(name = "mana")
    public static short mana;

    @Property
    public static short reserved_mana;

    @Property
    public static short reserved_health;

    @Property(name = "reserved_mana_mult", default_ = 1)
    public static short reserved_mana_multiplier;

    @Property(name = "reserved_health_mult", default_ = 1)
    public static short reserved_health_multiplier;

    @Property(default_ = 0.2f, name = "walk_speed")
    public static short walk_speed;

    @Property(default_ = 0, name = "walk_speed_bonus")
    public static short walk_speed_bonus;

    @Property(name = "experiences")
    public static short experiences;

    @Property(name = "experience_mult", default_ = 1)
    public static short experiences_mult;

    @Property(name = "health_regen", default_ = 1)
    public static short health_regen;

    @Property(name = "mana_regen", default_ = 1)
    public static short mana_regen;

    @Property(name = "health_cost_reduce", default_ = 1)
    public static short health_cost_reduce;

    @Property(name = "mana_cost_reduce", default_ = 1)
    public static short mana_cost_reduce;

    @Property(name = "cooldown_reduce", default_ = 1)
    public static short cooldown_reduce;

    @Property(name = "diamond_sword_bonus_damage", default_ = 0)
    public static short diamond_sword_bonus_damage;

    @Property(name = "golden_sword_bonus_damage", default_ = 0)
    public static short golden_sword_bonus_damage;

    @Property(name = "iron_sword_bonus_damage", default_ = 0)
    public static short iron_sword_bonus_damage;

    @Property(name = "wooden_sword_bonus_damage")
    public static short wooden_sword_bonus_damage;

    @Property(name = "diamond_axe_bonus_damage")
    public static short diamond_axe_bonus_damage;

    @Property(name = "golden_axe_bonus_damage")
    public static short golden_axe_bonus_damage;

    @Property(name = "iron_axe_bonus_damage")
    public static short iron_axe_bonus_damage;

    @Property(name = "wooden_axe_bonus_damage")
    public static short wooden_axe_bonus_damage;

    @Property(name = "diamond_pickaxe_bonus_damage")
    public static short diamond_pickaxe_bonus_damage;

    @Property(name = "golden_pickaxe_bonus_damage")
    public static short golden_pickaxe_bonus_damage;

    @Property(name = "iron_pickaxe_bonus_damage")
    public static short iron_pickaxe_bonus_damage;

    @Property(name = "wooden_pickaxe_bonus_damage")
    public static short wooden_pickaxe_bonus_damage;

    @Property(name = "diamond_hoe_bonus_damage")
    public static short diamond_hoe_bonus_damage;

    @Property(name = "golden_hoe_bonus_damage")
    public static short golden_hoe_bonus_damage;

    @Property(name = "iron_hoe_bonus_damage")
    public static short iron_hoe_bonus_damage;

    @Property(name = "wooden_hoe_bonus_damage")
    public static short wooden_hoe_bonus_damage;

    @Property(name = "swords_damage_mult", default_ = 1)
    public static short swords_damage_mult;

    @Property(name = "axes_damage_mult", default_ = 1)
    public static short axes_damage_mult;

    @Property(name = "pickaxes_damage_mult", default_ = 1)
    public static short pickaxes_damage_mult;

    @Property(name = "hoes_damage_mult", default_ = 1)
    public static short hoes_damage_mult;

    @Property(name = "bows_meele_damage_mult", default_ = 1)
    public static short bows_meele_damage_mult;

    @Property(name = "bow_meele_bonus_damage", default_ = 0)
    public static short bow_meele_bonus_damage;

    @Property(name = "fire_damage_bonus_mult", default_ = 1)
    public static short fire_damage_bonus_mult;

    @Property(name = "fire_damage_protection_mult", default_ = 1)
    public static short fire_damage_protection_mult;

    @Property(name = "physical_damage_protection_mult", default_ = 1)
    public static short physical_damage_protection_mult;

    @Property(name = "lightning_damage_bonus_mult", default_ = 1)
    public static short lightning_damage_bonus_mult;

    @Property(name = "lightning_damage_protection_mult", default_ = 1)
    public static short lightning_damage_protection_mult;

    @Property(name = "magic_damage_bonus_mult", default_ = 1)
    public static short magic_damage_bonus_mult;

    @Property(name = "magic_damage_protection_mult", default_ = 1)
    public static short magic_damage_protection_mult;

    @Property(name = "weapon_damage_bonus")
    public static short weapon_damage_bonus;

    @Property(name = "mana_regen_mult", default_ = 0)
    public static short mana_regen_mult;

    @Property(name = "ice_damage_protection_mult", default_ = 1)
    public static short ice_damage_protection_mult;

    @Property(name = "physical_damaga_bonus_mult",default_ = 1)
    public static short physical_damage_bonus_mult;

    @Property(name = "ice_damage_bonus_mult",default_ = 1)
    public static short ice_damage_bonus_mult;

    @Property(name = "staffs_damage_mult", default_ = 1)
    public static short staffs_damage_mult;
}
