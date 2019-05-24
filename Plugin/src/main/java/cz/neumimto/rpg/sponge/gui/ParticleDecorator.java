package cz.neumimto.rpg.sponge.gui;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.sponge.utils.math.VectorUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 9.7.2017.
 */
@Singleton
public class ParticleDecorator implements IActionDecorator {

    public static Vector3d[] smallCircle;
    public static Vector3d[] tinyCircle;
    public static Vector3d[][] smallCylinder;

    @Inject
    private NtRpgPlugin plugin;

    @Override
    public void strikeLightning(Location<World> location) {
        Entity q = location.getExtent().createEntity(EntityTypes.LIGHTNING, location.getPosition());
        location.getExtent().spawnEntity(q);
    }

    @Override
    public void createTrajectory(Entity entity, int interval, int maxticks, BiConsumer<Task, Entity> e) {
        Sponge.getScheduler()
                .createTaskBuilder()
                .delay(1L, TimeUnit.MILLISECONDS)
                .interval(interval, TimeUnit.MILLISECONDS)
                .execute((task -> {
                    if (!entity.isRemoved()) {
                        e.accept(task, entity);
                    } else {
                        task.cancel();
                    }
                })).submit(plugin);
    }

    @Override
    public void circle(Location location, int count, double radius, Consumer<Location> callback) {
        Extent e = location.getExtent();
        double increment = TrigMath.TWO_PI / count;
        for (int i = 0; i < count; i++) {
            double angle = i * increment;
            double x = location.getX() + radius * TrigMath.cos(angle);
            double z = location.getZ() + radius * TrigMath.sin(angle);
            callback.accept(new Location(e, x, location.getY(), z));
        }
    }

    @Override
    public void ellipse(Vector3d[] vector3ds, double a, double b, double vecmult, Vector3d rotationAngle) {
        double increment = TrigMath.TWO_PI / vector3ds.length;
        for (int i = 0; i < vector3ds.length; i++) {
            double angle = i * increment;
            Vector3d v = new Vector3d(a * TrigMath.cos(angle), b * TrigMath.sin(angle), 0).mul(vecmult);

            vector3ds[i] = VectorUtils.rotateAroundAxisY(v, -rotationAngle.getY());
        }

    }

    @Override
    public void spiral(double radius, double points, double fullrot,
                       double rotation,
                       Consumer<Vector3d> cb) {
        double a = radius / points;
        double s = fullrot / points;
        double arad = s * TrigMath.TWO_PI;
        rotation *= TrigMath.TWO_PI; //torad
        for (double i = 1; i <= points; i++) {
            double dist = i * a;
            double angle = i * arad + rotation;
            double x = TrigMath.cos(angle) * dist;
            double y = TrigMath.sin(angle) * dist;
            cb.accept(new Vector3d(x, 0, y));
        }
    }

    public void draw(Location world, Vector3d[] vector3ds, ParticleEffect effect) {
        for (Vector3d vector3d : vector3ds) {
            if (vector3d != null) {
                Location add = world.add(vector3d);
                draw(add, add.getPosition(), effect);
            }
        }
    }

    public void draw(Location<World> world, Vector3d vector3d, ParticleEffect particleEffect) {
        world.getExtent().spawnParticles(particleEffect, vector3d);
    }

    public void fillCircle(Vector3d[] d, double radius) {
        double increment = TrigMath.TWO_PI / d.length;
        for (int i = 0; i < d.length; i++) {
            double angle = i * increment;
            double x = radius * TrigMath.cos(angle);
            double z = radius * TrigMath.sin(angle);
            d[i] = new Vector3d(x, 0, z);
        }
    }

    public void initModels() {
        smallCircle = new Vector3d[15];
        fillCircle(smallCircle, 1.3);

        tinyCircle = new Vector3d[10];
        fillCircle(ParticleDecorator.tinyCircle, 0.5);

    }


}
