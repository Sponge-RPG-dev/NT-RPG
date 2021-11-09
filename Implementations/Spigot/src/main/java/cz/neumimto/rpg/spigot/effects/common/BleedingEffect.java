package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.nts.annotations.ScriptMeta.Handler;
import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEntity;
import de.slikey.effectlib.effect.BleedEffect;
import de.slikey.effectlib.util.RandomUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

@AutoService(IEffect.class)
@ScriptMeta.Function("BleedingEffect")
public class BleedingEffect extends EffectBase {

    public static String name = "Bleeding";
    private final IEntity consumer;
    private double damage;
    private BleedEffect effect;
    private LivingEntity livingEntity;

    @Handler
    public BleedingEffect(@NamedParam("target") IEntity consumer,
                          @NamedParam("d|duration") long duration,
                          @NamedParam("p|period") long period,
                          @NamedParam("d|damage") double damage) {
        super(name, consumer);
        setDuration(duration);
        setPeriod(period);
        this.damage = damage;
        livingEntity = (LivingEntity) consumer.getEntity();
        this.consumer = consumer;
    }


    @Override
    public void onTick(IEffect self) {
        Location location = livingEntity.getLocation().clone();
        location.add(0, RandomUtils.random.nextFloat() * 1.5, 0);
        location.getWorld().playEffect(location, Effect.STEP_SOUND, 178);
        Rpg.get().getDamageService().damageEntity(this.consumer, this.damage);
    }


    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }
}
