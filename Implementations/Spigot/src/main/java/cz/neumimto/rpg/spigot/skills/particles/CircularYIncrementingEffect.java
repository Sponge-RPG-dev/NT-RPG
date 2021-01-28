package cz.neumimto.rpg.spigot.skills.particles;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.CircleEffect;
import de.slikey.effectlib.util.VectorUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class CircularYIncrementingEffect extends CircleEffect {

    private double offsetY;

    public double offsetYIncrement;

    public CircularYIncrementingEffect(EffectManager effectManager) {
        super(effectManager);
    }

    @Override
    public void onRun() {
        Location location = getLocation();
        location.subtract(xSubtract, ySubtract, zSubtract);
        double inc = (2 * Math.PI) / particles;
        int steps = wholeCircle ? particles : 1;
        for (int i = 0; i < steps; i++) {
            double angle = step * inc;
            Vector v = new Vector();
            v.setX(Math.cos(angle) * radius);
            offsetY = offsetY + offsetYIncrement;
            v.setY(offsetY);
            v.setZ(Math.sin(angle) * radius);
            VectorUtils.rotateVector(v, xRotation, yRotation, zRotation);
            if (enableRotation) {
                VectorUtils.rotateVector(v, angularVelocityX * step, angularVelocityY * step, angularVelocityZ * step);
            }
            display(particle, location.clone().add(v), 0, 30);
            step++;
        }
    }
}
