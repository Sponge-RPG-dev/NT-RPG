package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.nts.annotations.ScriptMeta.Function;
import cz.neumimto.nts.annotations.ScriptMeta.Handler;
import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

@AutoService(IEffect.class)
@Function("UnhealEffect")
@Generate(id = "name", description = "Converts all incoming healing into damage")
public class UnhealEffect extends EffectBase<Float> {

    public static String name = "Unheal";

    @Handler
    public UnhealEffect(@NamedParam("e|entity") IEffectConsumer consumer,
                        @NamedParam("d|duration") long duration,
                        @NamedParam("m|multiplier") float multipler) {
        super(name, consumer);
        setDuration(duration);
        setValue(multipler);
    }

    public void process(LivingEntity entity, double healedAmount, SpigotDamageService damageService) {
        damageService.damage(entity, EntityDamageEvent.DamageCause.MAGIC, healedAmount * getValue(), false);

    }

}
