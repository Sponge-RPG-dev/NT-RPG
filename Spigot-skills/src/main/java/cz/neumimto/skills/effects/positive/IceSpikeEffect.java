package cz.neumimto.skills.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import de.slikey.effectlib.Effect;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;


public class IceSpikeEffect extends EffectBase {

    public static final String name = "IceSpike Aura";
    private final ISpigotCharacter caster;
    private final int radius;
    private final double damage;
    private final Effect effect;

    private SpigotDamageService damageService;
    private Entity entity;
    private Player player;

    public IceSpikeEffect(Entity spike, ISpigotCharacter caster, long duration, int radius, double damage, SpigotDamageService damageService, Effect effect) {
        super(name, caster);
        this.radius = radius;
        this.damage = damage;
        setPeriod(1000L);
        setDuration(duration);
        this.caster = caster;
        this.entity = spike;
        this.player = caster.getPlayer();
        this.damageService = damageService;
        this.effect = effect;
    }

    @Override
    public void onApply(IEffect self) {
        effect.start();
    }

    @Override
    public void onTick(IEffect self) {
        List<Entity> nearbyEntities = entity.getNearbyEntities(radius, radius, radius);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                if (damageService.canDamage(caster, livingEntity)) {
                    damageService.damage(player, livingEntity, EntityDamageEvent.DamageCause.MAGIC, damage, false);
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
                    livingEntity.getWorld().spawnParticle(Particle.SNOWBALL, livingEntity.getLocation(), 10, 3, 2, 3);
                }
            }
        }
    }

    @Override
    public void onRemove(IEffect self) {
        if (!entity.isDead())
            entity.remove();
        effect.cancel();
    }
}
