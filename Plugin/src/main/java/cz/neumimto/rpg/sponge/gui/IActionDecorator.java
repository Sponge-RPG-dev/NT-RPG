package cz.neumimto.rpg.sponge.gui;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 9.7.2017.
 */
public interface IActionDecorator {

    void strikeLightning(Location<World> location);

    void createTrajectory(Entity entity, int interval, int maxticks, BiConsumer<Task, Entity> e);

    void circle(Location location, int count, double radius, Consumer<Location> callback);

    void ellipse(Vector3d[] vector3ds, double a, double b, double vecmult, Vector3d rotationAngle);

    void spiral(double radius, double sides, double coils,
                double rotation,
                Consumer<Vector3d> cb);
}
