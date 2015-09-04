package cz.neumimto.skills;

import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Created by NeumimTo on 15.1.2015.
 */
public abstract class ProjectileProperties {
    public static Map<UUID, ProjectileProperties> cache = new HashMap<>();
    protected Projectile t;
    private double damage;
    private Living damager;
    public BiConsumer<Living, IActiveCharacter> consumer;

    public ProjectileProperties(Projectile t, Living damager) {
        this.t = t;
        this.damager = damager;
        cache.put(t.getUniqueId(), this);
    }

    public void onHit(BiConsumer<Living, IActiveCharacter> consumer) {
        this.consumer = consumer;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public Projectile getProjectile() {
        return t;
    }

}
