package cz.neumimto.rpg.sponge.skills;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by ja on 11.6.2017.
 */
public class ParticleUtils {

    private static GameRegistry registry;

    private static Game game;

    static {
        registry = Sponge.getGame().getRegistry();
        game = Sponge.getGame();
    }

    public static void drawCircle(Location<World> center, double radius, ParticleType type) {
        drawCircle(center.getExtent(), center.getX(), center.getY(), center.getZ(), radius,
                ParticleEffect.builder().type(type).build());
    }

    public static void drawCircle(World w, double x, double y, double z, double radius, ParticleEffect effect) {

    }

    public static void drawSquare(Location<World> location, int i, ParticleEffect effect) {
        for (int k = -i; k <= i; k++) {
            for (int z = -i; z <= i; z++) {
                drawParticle(location.getExtent(), location.getBlockZ() - z, location.getBlockY(), location.getBlockX() - k, effect);
            }
        }
    }

    private static void drawParticle(World w, int z, int blockY, int k, ParticleEffect effect) {
        w.spawnParticles(effect, new Vector3d(k, blockY, z));
    }
}
