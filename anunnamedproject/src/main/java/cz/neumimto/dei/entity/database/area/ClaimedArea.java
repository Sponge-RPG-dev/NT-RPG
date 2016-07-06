package cz.neumimto.dei.entity.database.area;

import cz.neumimto.dei.entity.AreaType;
import cz.neumimto.dei.entity.IHasClaims;
import cz.neumimto.dei.entity.database.utils.AreaPermissions;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.*;

/**
 * Created by NeumimTO on 5.7.2016.
 */
@MappedSuperclass
public class ClaimedArea<T extends IHasClaims> {

    private int x;

    private int z;

    private String world;

    @OneToOne(fetch=FetchType.EAGER , cascade=CascadeType.ALL)
    @JoinColumn(name="perms_id")
    private AreaPermissions areaPermissions;

    @Transient
    private AreaType areaType;

    public ClaimedArea() {

    }

    public ClaimedArea(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ClaimedArea(int x, int z, String world) {
        this(x, z);
        this.world = world;
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

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public static ClaimedArea of(int x, int z) {
        return new ClaimedArea(x,z);
    }

    public static ClaimedArea of(int x, int z,String world) {
        return new ClaimedArea(x,z,world);
    }

    @Override
    public int hashCode() {
        return x * 7963 + z * 6841;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClaimedArea that = (ClaimedArea) o;

        if (x != that.x) return false;
        if (z != that.z) return false;
        return world.equalsIgnoreCase(that.world);
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }

    public AreaPermissions getAreaPermissions() {
        return areaPermissions;
    }

    public void setAreaPermissions(AreaPermissions areaPermissions) {
        this.areaPermissions = areaPermissions;
    }

    public T getParent() {
        throw new NotImplementedException();
    };

    public void setParent(T parent) {
        throw new NotImplementedException();
    }
}
