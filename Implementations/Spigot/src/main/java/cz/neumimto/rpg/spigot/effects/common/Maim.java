package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.effects.common.model.SlowModel;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.CircleEffect;
import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;

@Generate(id = "name", description = "Decreases movement speed")
public class Maim extends SlowEffect {

    CircleEffect circleEffect;

    public Maim(IEffectConsumer consumer, long duration, SlowModel slowModel) {
        super(consumer, duration, slowModel);
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
