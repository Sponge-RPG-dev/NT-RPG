package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.*;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;

@Generate(id = "name", description = "Clears all effects impeding movement")
public class Rage extends EffectBase {

    public static final String name = "rage";

    public static Set<EffectType> removeTypes;
    RageEffect rageEffect;

    static {
        removeTypes = new HashSet<>();
        removeTypes.add(CommonEffectTypes.SLOW);
        removeTypes.add(CommonEffectTypes.STUN);
    }

    public Rage(IEffectConsumer consumer, long duration) {
        super(name, consumer);
        setDuration(duration);
    }

    @Override
    public void onApply(IEffect self) {
        Rpg.get().getEffectService().removeEffectsByType(self.getConsumer(), removeTypes);
        rageEffect = new RageEffect(SpigotRpgPlugin.getEffectManager());
        rageEffect.setDynamicTarget(new DynamicLocation((Entity) getConsumer().getEntity()));
        rageEffect.infinite();
        SpigotRpgPlugin.getEffectManager().start(rageEffect);
    }

    @Override
    public void onRemove(IEffect self) {
        rageEffect.cancel();
    }

    public static class RageEffect extends Effect {
        private double it = 0;
        private Location loc;

        public RageEffect(EffectManager effectManager) {
            super(effectManager);
            it = Math.PI / 16;
            period = 1;
        }

        @Override
        public void onRun() {
            it += Math.PI / 16;
            loc = getTarget();

            display(Particle.CLOUD,
                    loc.clone().add(Math.cos(it), Math.sin(it) + 1, Math.sin(it)),
                    Color.RED);

            display(Particle.REDSTONE,
                    loc.clone().add(Math.cos(it + Math.PI), Math.sin(it) + 1, Math.sin(it + Math.PI)),
                    Color.BLACK);
        }
    }
}
