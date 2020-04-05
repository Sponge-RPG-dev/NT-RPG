package cz.neumimto.rpg.spigot.utils;

import org.bukkit.util.Vector;

public class VectorUtils {

    public static Vector[] circle(Vector[] d, double radius) {
        double increment = (2 * Math.PI) / d.length;
        for (int i = 0; i < d.length; i++) {
            double angle = i * increment;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            d[i] = new Vector(x, 0, z);
        }
        return d;
    }
}
