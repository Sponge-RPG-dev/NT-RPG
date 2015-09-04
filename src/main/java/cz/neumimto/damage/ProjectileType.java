package cz.neumimto.damage;


import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

/**
 * Created by NeumimTo on 4.8.15.
 */
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
        if (type == EntityTypes.ARROW)
            return ARROW;
        if (type == EntityTypes.EGG)
            return EGG;
        if (type == EntityTypes.SNOWBALL)
            return SNOWBALL;
        if (type == EntityTypes.SMALL_FIREBALL)
            return SMALL_FIREBALL;
        if (type == EntityTypes.FIREBALL)
            return LARGE_FIREBALL;
        if (type == EntityTypes.ENDER_PEARL)
            return ENDER_PEARL;
        if (type == EntityTypes.WITHER_SKULL)
            return WITHER_SKULL;
        if (type == EntityTypes.THROWN_EXP_BOTTLE)
            return THROWN_EXP_BOTTLE;
        return CUSTOM;
    }
}
