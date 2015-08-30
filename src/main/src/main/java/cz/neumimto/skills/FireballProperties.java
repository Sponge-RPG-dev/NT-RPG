package cz.neumimto.skills;

import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;

/**
 * Created by NeumimTo on 13.3.2015.
 */
public class FireballProperties extends ProjectileProperties {
    public FireballProperties(Projectile t, Living damager) {
        super(t, damager);
    }

}
