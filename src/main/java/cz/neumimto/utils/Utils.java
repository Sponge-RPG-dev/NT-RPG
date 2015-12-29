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

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.GlobalScope;
import cz.neumimto.IEntity;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.entities.IMob;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.properties.PlayerPropertyService;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.function.Predicate;

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
        double s = Math.pow(radius, 2);
        HashSet<Entity> ee = new HashSet<>();
        for (Entity e : l.getExtent().getEntities()) {
            if (e.getLocation().getPosition().distanceSquared(l.getX(), l.getY(), l.getZ()) <= s) {
                ee.add(e);
            }
        }
        return ee;
    }


    public static Optional<Entity> spawnProjectile(IEntity caster, EntityType type) {
        return Optional.empty(); //todo
    }

    public static Optional<Entity> spawnInvisibleProjectile(IEntity caster, EntityType type) {
        return Optional.empty();
    }

    public static Set<BlockType> transparentBlocks = new HashSet<>();

    public static boolean isTransparent(BlockType e) {
        return true;
    }

    public static Living getTargettedEntity(IActiveCharacter character, int range) {
        Player player = character.getPlayer();
        Set<Entity> nearbyEntities = getNearbyEntities(player.getLocation(), range);
        Optional<BlockRayHit<World>> h = BlockRay.from(player).blockLimit(range).filter(BlockRay.onlyAirFilter()).build().end();
        if (h.isPresent()) {
            Vector3d lookPos = h.get().getBlockPosition().toDouble();
            Collection<Entity> entities = player.getWorld().getEntities(entity -> entity != player && entity.getLocation().getPosition().distanceSquared(lookPos) < 2 && isLivingEntity(entity));
            if (entities.isEmpty())
                return null;
            for (Entity e : entities) {
                return (Living) e;
            }
        }
        return null;
    }

    public static void hideProjectile(Projectile projectile) {
        projectile.offer(Keys.INVISIBLE, true);
    }

    public static String newLine(String s) {
        return Tab + s + LineSeparator;
    }

    /**
     * Resets stats of vanilla player object back to default state, Resets max hp, speed
     *
     * @param player
     */
    public static void resetPlayerToDefault(Player player) {
        player.offer(Keys.MAX_HEALTH, 20d);
        player.offer(Keys.HEALTH, 20d);
        player.offer(Keys.WALKING_SPEED, PlayerPropertyService.WALKING_SPEED);
    }

    /**
     * Inline negation of method references
     */
    public static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }


    //todo
    public static boolean canDamage(IActiveCharacter character, Living l) {
        return true;
    }

    public static boolean isLivingEntity(Entity entity) {
        return entity.get(Keys.HEALTH).isPresent();
    }

}
