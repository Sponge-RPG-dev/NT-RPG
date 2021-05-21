

package cz.neumimto.rpg.sponge.skills;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.utils.TriConsumer;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by NeumimTo on 15.1.2015.
 */
public class ProjectileProperties {
	/*public static Map<UUID, ProjectileProperties> cache = new LinkedHashMap<UUID, ProjectileProperties>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, ProjectileProperties> entry) {
            return entry.getValue().lifetime > System.currentTimeMillis();
        }
    };*/

    public static Map<Projectile, ProjectileProperties> cache = new WeakHashMap<>();
    public TriConsumer<DamageEntityEvent, IEntity, IEntity> consumer;
    //protected Projectile t;
    private double damage;
    // private long lifetime;
    private IEntity caster;

    public ProjectileProperties(Projectile t, IEntity caster) {
        //  this.t = t;
        cache.put(t, this);
        //   lifetime = System.currentTimeMillis()+5000;
        this.caster = caster;
    }

    public void onHit(TriConsumer<DamageEntityEvent, IEntity, IEntity> consumer) {
        this.consumer = consumer;
    }

    public void process(DamageEntityEvent event, IEntity target) {
        consumer.accept(event, caster, target);
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

}
