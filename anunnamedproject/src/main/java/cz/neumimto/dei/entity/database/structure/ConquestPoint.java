package cz.neumimto.dei.entity.database.structure;

import cz.neumimto.dei.entity.database.worldobject.Stronghold;
import cz.neumimto.dei.exceptions.WorldNotExistsException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.ColoredData;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.World;

import javax.persistence.*;
import java.util.Optional;

/**
 * Created by ja on 5.7.2016.
 */
@Entity
@Table(name = "dei_contquest_points")
public class ConquestPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int x;

    private int z;

    private int y;

    private String world;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Stronghold stronghold;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Color getColor() {
        Optional<World> world = Sponge.getGame().getServer().getWorld(this.world);
        if (world.isPresent()) {
            World world1 = world.get();
            BlockSnapshot snapshot = world1.createSnapshot(x,y,z);
            Optional<Color> color = snapshot.get(Keys.COLOR);
            return color.isPresent() ? color.get() : null;
        }
        throw new WorldNotExistsException(this.world);
    }

    public void setColor(Color data) {
        Optional<World> world = Sponge.getGame().getServer().getWorld(this.world);
        if (world.isPresent()) {
            World world1 = world.get();
            BlockSnapshot snapshot = world1.createSnapshot(x,y,z);
            //todo

        }
        throw new WorldNotExistsException(this.world);
    }

}
