package cz.neumimto.utils;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Predicate;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 25.7.2015.
 */
public class Utils {

    public static String LineSeparator = System.getProperty("line.separator");
    public static String Tab = "\t";

    public static double getPercentage(double n, double total) {
        return (n / total) * 100;
    }

    public static boolean isMoreThanPercentage(double a, double b, double percentage) {
        return ((a / b) * 100 - 100) >= percentage;
    }

    public static Set<Entity> getNearbyEntities(Location l, int radius){
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
        Set<Entity> radiusEntities = new HashSet<>();
        double squared = Math.pow(radius,2);

        for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
            for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
                int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
                for (Entity e : new Location(l.getExtent(),x+(chX*16),y,z+(chZ*16)).getExtent().getEntities()){
                    if (getDistanceSquared(e.getLocation(),l) <= squared && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
                }
            }
        }
        return radiusEntities;
    }

    public static double getDistanceSquared(Location origin, Location target) {
        double dx = origin.getX() - target.getX();
        double dy = origin.getY() - target.getY();
        double dz = origin.getZ() - target.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    public static void createUnbreakableBlock(Location location) {

    }
}
