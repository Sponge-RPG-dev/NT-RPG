package cz.neumimto.rpg.spigot.skills.utils;

import de.slikey.effectlib.util.VectorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

public class Decorator {

    public static void circle(Location center,
                       int radius,
                       int particleCount,
                       Particle effect,
                       int count,
                       double offsetx,
                       double offsety,
                       double offsetz,
                       int data
                       ) {
        center = center.clone();
        final double pitch = (center.getPitch() + 90.0F) * Math.PI/180;
        final double yaw = -center.getYaw() * Math.PI/180;
        double increment = (2 * Math.PI) / particleCount;
        for (int i = 0; i < particleCount; i++) {
            double angle = i * increment;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Vector vec = new Vector(x, 0, z);

            VectorUtils.rotateAroundAxisX(vec, pitch);
            VectorUtils.rotateAroundAxisY(vec, yaw);

            center.add(vec);
            center.getWorld().spawnParticle(effect, center, count, offsetx, offsety, offsetz, data);
            center.subtract(vec);
        }
    }
    /* JS cannot handler overloaded methods well, we would need to do "point["args array type"](actual call)",
     therefore the indexes */
    public static void point4(Location location,
                      Particle effect,
                      int count,
                      double offsetx,
                      double offsety,
                      double offsetz,
                      int data
    ) {
        location.getWorld().spawnParticle(effect, location, count, offsetx, offsety, offsetz, data);
    }

    public static void point0(Location location, Particle effect, int count) {
        location.getWorld().spawnParticle(effect, location, count);
    }

    public static void point1(Location location, Particle effect, int count, Material material) {
        location.getWorld().spawnParticle(effect, location, count, material.data);
    }

    public static void point2(Location location, Particle effect, int count, Material bd) {
        location.getWorld().spawnParticle(effect, location, count, bd.createBlockData());
    }

}
