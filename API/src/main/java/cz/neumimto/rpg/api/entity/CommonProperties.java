package cz.neumimto.rpg.api.entity;

import cz.neumimto.rpg.api.properties.Property;
import cz.neumimto.rpg.api.properties.PropertyContainer;

@PropertyContainer
public class CommonProperties {


    @Property(name = "mana")
    public static int mana;

    @Property(name = "max_health", default_ = 1)
    public static int max_health;

    @Property(name = "max_mana")
    public static int max_mana;

    @Property(name = "reserved_health")
    public static int reserved_health;

    @Property(name = "reserved_mana")
    public static int reserved_mana;

    @Property(name = "reserved_health_mult", default_ = 1)
    public static int reserved_health_multiplier;

    @Property(name = "reserved_mana_mult", default_ = 1)
    public static int reserved_mana_multiplier;

    @Property(name = "health_regen", default_ = 1)
    public static int health_regen;

    @Property(name = "mana_regen")
    public static int mana_regen;

    @Property(name = "mana_regen_mult", default_ = 1)
    public static int mana_regen_mult;

    @Property(name = "experience_mult", default_ = 1)
    public static int experiences_mult;

    @Property(name = "health_cost_mult", default_ = 1)
    public static int health_cost_reduce;

    @Property(name = "mana_cost_mult", default_ = 1)
    public static int mana_cost_reduce;

    @Property(name = "cooldown_reduce_mult", default_ = 1)
    public static int cooldown_reduce_mult;

    @Property(name = "weapon_damage_bonus")
    public static int weapon_damage_bonus;

    @Property(name = "walk_speed", default_ = 0.1f)
    public static int walk_speed;

    @Property(name = "physical_damage_bonus_mult", default_ = 1)
    public static int physical_damage_bonus_mult;

    @Property(name = "magic_damage_bonus_mult", default_ = 1)
    public static int magic_damage_bonus_mult;

    @Property(name = "fire_damage_bonus_mult", default_ = 1)
    public static int fire_damage_bonus_mult;

    @Property(name = "lightning_damage_bonus_mult", default_ = 1)
    public static int lightning_damage_bonus_mult;

    @Property(name = "all_skills_bonus")
    public static int all_skills_bonus;

    @Property(name = "projectile_damage_bonus", default_ = 1)
    public static int projectile_damage_bonus;

    @Property(name = "arrow_damage_mult", default_ = 1)
    public static int arrow_damage_mult;
}
