package cz.neumimto.rpg.spigot.packetwrapper;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import cz.neumimto.rpg.spigot.skills.utils.AbstractPacket;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public class WrapperPlayServerEntityMetadata extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;

    public WrapperPlayServerEntityMetadata() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityMetadata(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve unique entity ID to update.
     *
     * @return The current Entity ID
     */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set unique entity ID to update.
     *
     * @param value - new value.
     */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the entity.
     *
     * @param world - the current world of the entity.
     * @return The entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the entity.
     *
     * @param event - the packet event.
     * @return The entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieve a list of all the watchable objects.
     * <p>
     * This can be converted to a data watcher using {@link WrappedDataWatcher#WrappedDataWatcher(List) WrappedDataWatcher(List)}
     *
     * @return The current metadata
     */
    public List<WrappedWatchableObject> getEntityMetadata() {
        return handle.getWatchableCollectionModifier().read(0);
    }

    /**
     * Set the list of the watchable objects (meta data).
     *
     * @param value - new value.
     */
    public void setEntityMetadata(List<WrappedWatchableObject> value) {
        handle.getWatchableCollectionModifier().write(0, value);
    }
}
