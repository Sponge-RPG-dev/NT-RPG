package cz.neumimto.rpg.spigot.entities;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.utils.TriConsumer;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class ProjectileCache {


    public static Map<Projectile, ProjectileCache> cache = new WeakHashMap<>();

    public TriConsumer<EntityDamageByEntityEvent, IEntity, IEntity> consumer;
    //protected Projectile t;
    private double damage;
    // private long lifetime;
    private IEntity caster;

    private ProjectileCache(Projectile t, IEntity caster) {
        cache.put(t, this);
        this.caster = caster;
    }

    public static ProjectileCache putAndGet(Projectile t, IEntity caster) {
        return new ProjectileCache(t, caster);
    }

    public void onHit(TriConsumer<EntityDamageByEntityEvent, IEntity, IEntity> consumer) {
        this.consumer = consumer;
    }

    public void process(EntityDamageByEntityEvent event, IEntity target) {
        consumer.accept(event, caster, target);
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
