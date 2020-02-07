package cz.neumimto.skills.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;


public class IceSpikeAura extends EffectBase {

    public static final String name = "Ice Aura";
    private final ISpigotCharacter caster;
    private final int radius;
    private final double damage;

    private SpigotDamageService damageService;
    private Entity entity;
    private Player player;

    public IceSpikeAura(Entity spike, ISpigotCharacter caster, long duration, int radius, double damage) {
        super(name, caster);
        this.radius = radius;
        this.damage = damage;
        setPeriod(1000L);
        setDuration(duration);
        this.caster = caster;
        this.entity = spike;
        this.player = caster.getPlayer();
    }

    @Override
    public void onApply(IEffect self) {

    }

    @Override
    public void onTick(IEffect self) {

        List<Entity> nearbyEntities = entity.getNearbyEntities(radius, radius, radius);
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) nearbyEntity;
                if (damageService.canDamage(caster, livingEntity)) {
                    damageService.damage(player, livingEntity, EntityDamageEvent.DamageCause.MAGIC, damage, false);
                }
            }
        }
    }

    @Override
    public void onRemove(IEffect self) {
        if (!entity.isDead())
            entity.remove();
    }
}
