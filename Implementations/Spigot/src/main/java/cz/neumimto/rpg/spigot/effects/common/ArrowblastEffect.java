package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.utils.MathUtils;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.ProjectileCache;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

@AutoService(IEffect.class)
@ScriptMeta.Function("ArrowblastEffect")
public class ArrowblastEffect extends EffectBase {

    public static String name = "Arrowblast";
    private final int arrows;
    private final double damage;
    private final double damageMax;
    private final long velocity;

    @ScriptMeta.Handler
    public ArrowblastEffect(@NamedParam("c|consumer") IEffectConsumer consumer,
                            @NamedParam("a|arrows") int arrows,
                            @NamedParam("dmin|damage-min") double damage,
                            @NamedParam("dmax|damage-max") double damageMax,
                            @NamedParam("p|period") long period,
                            @NamedParam("v|velocity") long velocity) {
        super(name, consumer);
        this.arrows = arrows;
        this.damage = damage;
        this.damageMax = damageMax;
        this.velocity = velocity;
        setPeriod(period);
        setDuration(period * arrows + 1);
    }

    @Override
    public void onTick(IEffect self) {
        LivingEntity livingEntity = (LivingEntity) getConsumer().getEntity();

        World world = livingEntity.getWorld();
        Location location = livingEntity.getLocation();
        Arrow arrow = (Arrow) world.spawnEntity(location.clone().add(0, 1, 0).add(location.getDirection()), EntityType.ARROW);
        arrow.setVelocity(livingEntity.getLocation().getDirection().multiply(velocity));
        arrow.setShooter(livingEntity);


        ProjectileCache projectileProperties = ProjectileCache.putAndGet(arrow, (ISpigotEntity) getConsumer());
        projectileProperties.onHit((event, attacker, target) -> event.setDamage(MathUtils.randomInRange(damage, damageMax)));
    }
}
