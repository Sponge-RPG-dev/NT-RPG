package cz.neumimto.rpg.spigot.skills.utils;

import org.bukkit.Location;
import org.bukkit.Particle;
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
    public double radius;

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

    public void draw() {
        int particleDistance = 1;

        int _minX = (int) (minX * 100);
        int _minY = (int) (minY * 100);
        int _minZ = (int) (minZ * 100);
        int _maxX = (int) (maxX * 100);
        int _maxY = (int) (maxY * 100);
        int _maxZ = (int) (maxZ * 100);

        for (double x = _minX; x <= _maxX; x += particleDistance) {
            for (double y = _minY; y <= _maxY; y += particleDistance) {
                for (double z = _minZ; z <= _maxZ; z += particleDistance) {
                    boolean edge = false;
                    if ((x == _minX || x == _maxX) && (y == _minY || y == _maxY)) edge = true;
                    if ((z == _minZ || z == _maxZ) && (y == _minY || y == _maxY)) edge = true;
                    if ((x == _minX || x == _maxX) && (z == _minZ || z == _maxZ)) edge = true;

                    if (edge) {
                        world.spawnParticle(Particle.VILLAGER_HAPPY,
                                new Location(world, x / 100, y / 100, z / 100),
                                1, 0, 0, 0,
                                0);
                    }
                }
            }
        }
    }

    private boolean overlaps(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.minX < maxX && this.maxX > minX
                && this.minY < maxY && this.maxY > minY
                && this.minZ < maxZ && this.maxZ > minZ;
    }

    public boolean overlaps(BoundingBox other) {
        return overlaps(other.getMinX(), other.getMinY(), other.getMinZ(), other.getMaxX(), other.getMaxY(), other.getMaxZ());
    }
}
