/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.utils;

import cz.neumimto.GlobalScope;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.players.IActiveCharacter;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.world.Location;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 25.7.2015.
 */
public class Utils {

    private static GlobalScope globalScope = NtRpgPlugin.GlobalScope;
    public static String LineSeparator = System.getProperty("line.separator");
    public static String Tab = "\t";

    public static double getPercentage(double n, double total) {
        return (n / total) * 100;
    }

    public static boolean isMoreThanPercentage(double a, double b, double percentage) {
        return ((a / b) * 100 - 100) >= percentage;
    }

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static double round(float value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return Math.round(value * scale) / scale;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static Set<Entity> getNearbyEntities(Location l, int radius) {
        int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
        Set<Entity> radiusEntities = new HashSet<>();
        double squared = Math.pow(radius, 2);

        for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
            for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
                int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
                for (Entity e : new Location(l.getExtent(), x + (chX * 16), y, z + (chZ * 16)).getExtent().getEntities()) {
                    if (getDistanceSquared(e.getLocation(), l) <= squared && e.getLocation().getBlock() != l.getBlock())
                        radiusEntities.add(e);
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

    public static boolean canDamage(Player player, Living entity) {
        return true;
    }

    public static Living getTargettedEntity(IActiveCharacter character, double range) {
        throw new NotImplementedException("Utils.getTargettedEntity - not implemented yet");
    }

    public static void hideProjectile(Projectile projectile) {
        projectile.offer(Keys.INVISIBLE,true);
    }

    public static String newLine(String s) {
        return Tab + s + LineSeparator;
    }

    /**
     * Resets stats of vanilla player object back to default state, Resets max hp, speed
     * @param player
     */
    public static void resetPlayerToDefault(Player player) {
        player.offer(Keys.MAX_HEALTH, 20d);
        player.offer(Keys.HEALTH, 20d);
        //player walkspeed
        //player.offer(Keys.WALK_SPEED,0.2d);
    }
}
