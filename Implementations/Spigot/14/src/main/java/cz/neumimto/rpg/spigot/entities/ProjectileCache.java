package cz.neumimto.rpg.spigot.entities;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.utils.TriConsumer;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;
import java.util.WeakHashMap;

public class ProjectileCache {

    public static Map<Projectile, ProjectileCache> cache = new WeakHashMap<>();

    public TriConsumer<EntityDamageByEntityEvent, ISpigotEntity, ISpigotEntity> consumer;
    // private long lifetime;
    private ISpigotEntity caster;
    private PlayerSkillContext skill;

    private ProjectileCache(Projectile t, ISpigotEntity caster) {
        cache.put(t, this);
        this.caster = caster;
    }

    public static ProjectileCache putAndGet(Projectile t, ISpigotEntity caster) {
        return new ProjectileCache(t, caster);
    }

    public void onHit(TriConsumer<EntityDamageByEntityEvent, ISpigotEntity, ISpigotEntity> consumer) {
        this.consumer = consumer;
    }

    public void process(EntityDamageByEntityEvent event, ISpigotEntity target) {
        consumer.accept(event, caster, target);
    }

    public void setSkill(PlayerSkillContext info) {
        this.skill = info;
    }

    public PlayerSkillContext getSkill() {
        return skill;
    }
}
