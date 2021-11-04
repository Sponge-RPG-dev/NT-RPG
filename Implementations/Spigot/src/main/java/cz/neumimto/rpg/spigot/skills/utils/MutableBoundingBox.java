package cz.neumimto.rpg.spigot.skills.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class MutableBoundingBox {
    private World world;
    private double minX;
    private double minY;
    private double minZ;
    private double maxX;
    private double maxY;
    private double maxZ;
    private double radius;

    public MutableBoundingBox(Location currentLoc, double radius) {
        this.radius = radius;
        moveAt(currentLoc);
    }

    public void moveAt(Location currentLoc) {
        minX = currentLoc.getX() - radius;
        minY = currentLoc.getY() - radius;
        minZ = currentLoc.getZ() - radius;
        maxX = currentLoc.getX() + radius;
        maxY = currentLoc.getY() + radius;
        maxZ = currentLoc.getZ() + radius;

        world = currentLoc.getWorld();
    }

    public boolean overlaps(Location point) {
        return point.getX() >= this.minX && point.getX() < this.maxX
                && point.getY() >= this.minY && point.getY() < this.maxY
                && point.getZ() >= this.minZ && point.getZ() < this.maxZ;
    }
}
