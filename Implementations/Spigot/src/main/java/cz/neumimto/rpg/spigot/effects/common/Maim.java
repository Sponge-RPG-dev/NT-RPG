package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.effects.common.model.SlowModel;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.CircleEffect;
import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;

@ScriptMeta.Function("MainEffect")
@AutoService(IEffect.class)
@Generate(id = "name", description = "Decreases movement speed")
public class Maim extends SlowEffect {

    CircleEffect circleEffect;

    @Generate.Constructor
    public Maim(IEffectConsumer consumer, long duration, @Generate.Model SlowModel slowModel) {
        super(consumer, duration, slowModel);
        name = "maim";
    }

    @ScriptMeta.Handler
    public Maim(@ScriptMeta.NamedParam("e|entity") IEffectConsumer consumer,
                @ScriptMeta.NamedParam("d|duration") long duration,
                @ScriptMeta.NamedParam("sL|slowLevel") int slowLevel,
                @ScriptMeta.NamedParam("jh|jumpHeight") boolean jh) {
        super(consumer, duration, new SlowModel(slowLevel, jh));
        name = "maim";
    }

    @Override
    public void onApply(IEffect self) {
        super.onApply(self);

        circleEffect = new CircleEffect(SpigotRpgPlugin.getEffectManager());
        circleEffect.type = EffectType.REPEATING;
        circleEffect.particles = 30;
        circleEffect.particle = Particle.REDSTONE;
        circleEffect.wholeCircle = true;
        circleEffect.asynchronous = true;
        circleEffect.duration = (int) getDuration();
        circleEffect.setDynamicTarget(new DynamicLocation((Entity) getConsumer().getEntity()));
        SpigotRpgPlugin.getEffectManager().start(circleEffect);

    }

    @Override
    public void onRemove(IEffect self) {
        super.onRemove(self);
        circleEffect.cancel();
    }
}
