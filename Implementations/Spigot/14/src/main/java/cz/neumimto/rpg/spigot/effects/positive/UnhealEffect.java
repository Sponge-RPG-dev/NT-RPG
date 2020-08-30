package cz.neumimto.rpg.spigot.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

@Generate(id = "name", description = "Converts all incoming healing into damage")
public class UnhealEffect extends EffectBase<Float> {

    public static String name = "Unheal";

    public UnhealEffect(IEffectConsumer consumer, long duration, float multipler) {
        super(name, consumer);
        setDuration(duration);
        setValue(multipler);
    }

    public void process(LivingEntity entity, double healedAmount, SpigotDamageService damageService) {
        damageService.damage(entity, EntityDamageEvent.DamageCause.MAGIC, healedAmount * getValue(), false);

    }

}
