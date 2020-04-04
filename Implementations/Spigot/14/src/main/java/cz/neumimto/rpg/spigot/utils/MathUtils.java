package cz.neumimto.rpg.spigot.utils;

import org.bukkit.util.Vector;

public class MathUtils {

    public static Vector calculateVelocityForParabolicMotion(Vector from, Vector to, double heightGain) {
        double gravity = 0.667;
        double endGain = to.getY() - from.getY();
        double horizDist = Math.sqrt(distanceSquared(from, to));
        double maxGain = Math.max(heightGain, (endGain + heightGain));

        double a = -horizDist * horizDist / (4 * maxGain);
        double c = -endGain;
        double slope = -horizDist / (2 * a) - Math.sqrt(horizDist * horizDist - 4 * a * c) / (2 * a);
        double vy = Math.sqrt(maxGain * gravity);
        double vh = vy / slope;
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;

        double vx = vh * dirx;
        double vz = vh * dirz;
        return new Vector(vx, vy, vz);
    }

    private static double distanceSquared(Vector from, Vector to) {
        double dx = to.getBlockX() - from.getBlockX();
        double dz = to.getBlockZ() - from.getBlockZ();
        return dx * dx + dz * dz;
    }
}
