package cz.neumimto.rpg.spigot.packetwrapper;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.skills.utils.AbstractPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
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

    public static List<AbstractPacket> spawn(Location location, ItemStack itemStack, EquipmentSlot slot, float yaw) {
        return factory._spawn(location, itemStack, slot, yaw, Long.MAX_VALUE);
    }

    public static List<AbstractPacket> amorStand(Location location, ItemStack itemStack, EquipmentSlot slot, float yaw, long duration) {
        List<AbstractPacket> list = factory._spawn(location, itemStack, slot, yaw, duration);
        return list;
    }

    public static AbstractPacket animateMainHand(Player entity) {
        WrapperPlayServerAnimation animation = new WrapperPlayServerAnimation();
        animation.setEntityID(entity.getEntityId());
        animation.setAnimation(WrapperPlayServerAnimation.Animations.SWING_ARM);
        return animation;
    }

    private LinkedList<AbstractPacket> _spawn(Location location, ItemStack itemStack, EquipmentSlot slot, float yaw, long duration) {
        WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
        spawn.setEntityID(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));

        entityLifeTime.put(spawn.getEntityID(), System.currentTimeMillis() + duration);

        spawn.setX(location.getX());
        spawn.setY(location.getY());
        spawn.setZ(location.getZ());
        //spawn.setType(EntityType.ARMOR_STAND);
        spawn.getHandle().getIntegers().write(1, 1);

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(spawn.getEntityID());
        metadata.setMetadata(fillMetadata(metadata));;

        WrapperPlayServerEntityLook rotation = new WrapperPlayServerEntityLook();
        rotation.setEntityID(spawn.getEntityID());
        rotation.setYaw(yaw);
        rotation.setPitch(0);

        WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment();
        equipment.setEntityID(spawn.getEntityID());
        //equipment.setSlot(EnumWrappers.ItemSlot.HEAD);
        //equipment.setItem(itemStack);
        equipment.setContents(Collections.singletonList(new Pair<>(EnumWrappers.ItemSlot.HEAD, itemStack)));

        LinkedList<AbstractPacket> list = new LinkedList();
        list.add(spawn);
        list.add(rotation);
        list.add(equipment);
        list.add(metadata);
        return list;
    }

    private List<WrappedWatchableObject> fillMetadata(WrapperPlayServerEntityMetadata metadata) {

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(metadata.getMetadata());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x20 )); //invis
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(11, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01 | 0x08 | 0x10)); //isSmall, noBasePlate, set Marker

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
                WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
                destroy.setEntities(toDestroy);
                Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                for (Player onlinePlayer : onlinePlayers) {
                    destroy.sendPacket(onlinePlayer);
                }
            }
        }
    }
}
