package cz.neumimto.rpg.spigot.skills.particles;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.CircleEffect;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.concurrent.ThreadLocalRandom;

public class StaticCircularBeamEffect extends CircleEffect {

    public double vertVecMin;
    public double vertVecMax;

    public double vertStep = 0.1;

    public StaticCircularBeamEffect(EffectManager effectManager) {
        super(effectManager);
    }


    @Override
    protected void display(Particle particle, Location location, Color color, float speed, int amount) {
        double d = ThreadLocalRandom.current().nextDouble(vertVecMin, vertVecMax);

        double y = location.getY();
        for (double i = 0; i < d; i = +vertStep) {
            location.add(0, i, 0);
            effectManager.display(particle, location, particleOffsetX, particleOffsetY, particleOffsetZ, speed, amount,
                    particleSize, color, material, materialData, visibleRange, targetPlayers);
        }
        location.setY(y);
    }


}
