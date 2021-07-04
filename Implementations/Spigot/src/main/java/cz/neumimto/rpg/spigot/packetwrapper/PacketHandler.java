package cz.neumimto.rpg.spigot.packetwrapper;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import cz.neumimto.rpg.spigot.Resourcepack;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.skills.utils.AbstractPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class PacketHandler {

    private static ConcurrentHashMap<Integer, Long> entityLifeTime = new ConcurrentHashMap<>();

    static PacketHandler factory;

    static {
        factory = new PacketHandler();
    }

    private PacketHandler() {

    }

    public static List<AbstractPacket> spawn(Location location, ItemStack itemStack, EnumWrappers.ItemSlot slot, float yaw) {
        return factory._spawn(location, itemStack, slot, yaw, Long.MAX_VALUE);
    }

    public static List<AbstractPacket> amorStand(Location location, ItemStack itemStack, EnumWrappers.ItemSlot slot, float yaw, long duration) {
        List<AbstractPacket> list = factory._spawn(location, itemStack, slot, yaw, duration);
        return list;
    }

    public static AbstractPacket animateMainHand(Player entity) {
        WrapperPlayServerAnimation animation = new WrapperPlayServerAnimation();
        animation.setEntityID(entity.getEntityId());
        animation.setAnimation(WrapperPlayServerAnimation.Animations.SWING_ARM);
        return animation;
    }

    public static AbstractPacket riptide(LivingEntity entity) {
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityId(entity.getEntityId());

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(metadata.getEntityMetadata());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x04));
        metadata.setEntityMetadata(dataWatcher.getWatchableObjects());

        return metadata;
    }

    public static AbstractPacket riptideEnd/* ? */(LivingEntity entity) {
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityId(entity.getEntityId());

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(metadata.getEntityMetadata());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(7, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01));
        metadata.setEntityMetadata(dataWatcher.getWatchableObjects());

        return metadata;
    }

    private LinkedList<AbstractPacket> _spawn(Location location, ItemStack itemStack, EnumWrappers.ItemSlot slot, float yaw, long duration) {
        WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
        spawn.setEntityID(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));

        entityLifeTime.put(spawn.getEntityID(), System.currentTimeMillis() + duration);

        spawn.setX(location.getX());
        spawn.setY(location.getY());
        spawn.setZ(location.getZ());
        //spawn.setType(EntityType.ARMOR_STAND);
        spawn.getHandle().getIntegers().write(1, 1);

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityId(spawn.getEntityID());
        metadata.setEntityMetadata(fillMetadata(metadata));
        ;

        WrapperPlayServerEntityLook rotation = new WrapperPlayServerEntityLook();
        rotation.setEntityID(spawn.getEntityID());
        rotation.setYaw(yaw);
        rotation.setPitch(0);

        WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment();
        equipment.setEntityID(spawn.getEntityID());

        equipment.setContents(Collections.singletonList(new Pair<>(slot, itemStack)));

        LinkedList<AbstractPacket> list = new LinkedList();
        list.add(spawn);
        list.add(rotation);
        list.add(equipment);
        list.add(metadata);
        return list;
    }

    private List<WrappedWatchableObject> fillMetadata(WrapperPlayServerEntityMetadata metadata) {

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(metadata.getEntityMetadata());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x20)); //invis

        return dataWatcher.getWatchableObjects();
    }

    public static void init() {
        new Despawn().runTaskTimerAsynchronously(SpigotRpgPlugin.getInstance(), 1, 250);
    }

    private static class Despawn extends BukkitRunnable {

        @Override
        public void run() {
            Iterator<Map.Entry<Integer, Long>> iterator = entityLifeTime.entrySet().iterator();

            long l = System.currentTimeMillis();

            List<Integer> toDestroy = new ArrayList<>();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Long> next = iterator.next();
                if (next.getValue() <= l) {
                    toDestroy.add(next.getKey());
                }
            }

            if (!toDestroy.isEmpty()) {
                destroy(toDestroy, Bukkit.getServer().getOnlinePlayers());
            }
        }
    }

    public static void destroy(List<Integer> entityIds, Collection<? extends Player> players) {
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntities(entityIds);
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player onlinePlayer : onlinePlayers) {
            destroy.sendPacket(onlinePlayer);
        }
    }

    public static int randomGroundIcicle(Location location, List<AbstractPacket> packets) {
        WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
        spawn.setEntityID(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        spawn.setX(location.getX() + ThreadLocalRandom.current().nextInt(100) / 100);
        spawn.setY(location.getY());
        spawn.setZ(location.getZ() + ThreadLocalRandom.current().nextInt(100) / 100);
        spawn.getHandle().getIntegers().write(1, 1);
        packets.add(spawn);

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityId(spawn.getEntityID());

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(metadata.getEntityMetadata());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x20)); //invis
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01 | 0x08 | 0x10)); //isSmall, noBasePlate, set Marker

        metadata.setEntityMetadata(dataWatcher.getWatchableObjects());
        packets.add(metadata);

        WrapperPlayServerEntityLook rotation = new WrapperPlayServerEntityLook();
        rotation.setEntityID(spawn.getEntityID());
        rotation.setYaw(ThreadLocalRandom.current().nextInt(360));
        rotation.setPitch(0);
        packets.add(rotation);

        WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment();
        equipment.setEntityID(spawn.getEntityID());
        equipment.setContents(Collections.singletonList(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, Resourcepack.randomIcicle())));
        packets.add(equipment);

        return spawn.getEntityID();
    }
}
