package cz.neumimto.dei.entity.database.area;

import cz.neumimto.dei.entity.database.worldobject.Stronghold;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Color;

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
}
