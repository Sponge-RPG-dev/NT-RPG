package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.IEntity;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

@ScriptMeta.Function("FlickerEffect")
@AutoService(IEffect.class)
@Generate(id = "name", description = "Entity rapidly toggle its visible/invisible state")
public class FlickerEffect extends EffectBase {

    public static String name = "Flicker Effect";
    private final long invisDuration;


    @ScriptMeta.Handler
    public FlickerEffect(@ScriptMeta.NamedParam("e|entity") IEffectConsumer consumer,
                         @ScriptMeta.NamedParam("d|duration") long duration,
                         @ScriptMeta.NamedParam("id|invisDuration") long invisDuration) {
        super(name, consumer);
        setDuration(duration);
        this.invisDuration = invisDuration;
    }

    @Override
    public void onTick(IEffect self) {
        InvisibilityEffect invisibilityEffect = new InvisibilityEffect(getConsumer(), invisDuration);
        Rpg.get().getEffectService().addEffect(invisibilityEffect, getEffectSourceProvider(), (IEntity) getConsumer());
        LivingEntity entity = (LivingEntity) getConsumer().getEntity();
        entity.getWorld().spawnParticle(Particle.SMOKE_NORMAL, entity.getLocation(), 5);
    }

}
