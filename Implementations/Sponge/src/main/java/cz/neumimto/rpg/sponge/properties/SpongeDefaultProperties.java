

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
