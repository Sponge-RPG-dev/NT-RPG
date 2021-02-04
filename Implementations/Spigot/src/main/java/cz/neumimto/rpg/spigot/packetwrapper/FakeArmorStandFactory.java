package cz.neumimto.rpg.spigot.packetwrapper;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FakeArmorStandFactory {

    private static Map<Integer, Long> entityDescriptionTime = new HashMap<>();

    static FakeArmorStandFactory factory;

    static {
        factory = new FakeArmorStandFactory();
    }

    private FakeArmorStandFactory() {

    }

    public static void spawn(Location location, ItemStack itemStack, EquipmentSlot slot, Collection<? extends Player> players) {
        factory._spawn(location, itemStack, slot, players);
    }

    public static void spawnWithLifespan(Location location, ItemStack itemStack, EquipmentSlot slot, Collection<? extends Player> players, long millis) {
        int i = factory._spawn(location, itemStack, slot, players);
        entityDescriptionTime.put(i, System.currentTimeMillis() + millis);
    }

    private int _spawn(Location location, ItemStack itemStack, EquipmentSlot slot, Collection<? extends Player> players) {
        WrapperPlayServerSpawnEntityLiving spawn = new WrapperPlayServerSpawnEntityLiving();
        spawn.setEntityID(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        spawn.setX(location.getX());
        spawn.setY(location.getY());
        spawn.setZ(location.getZ());
        //spawn.setType(EntityType.ARMOR_STAND);
        spawn.getHandle().getIntegers().write(1, 1);

        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
        metadata.setEntityID(spawn.getEntityID());
        metadata.setMetadata(fillMetadata(metadata));;


        WrapperPlayServerEntityEquipment equipment = new WrapperPlayServerEntityEquipment();
        equipment.setEntityID(spawn.getEntityID());
        //equipment.setSlot(EnumWrappers.ItemSlot.HEAD);
        //equipment.setItem(itemStack);
        equipment.setContents(Collections.singletonList(new Pair<>(EnumWrappers.ItemSlot.HEAD, itemStack)));


        for (Player player : players) {
            spawn.sendPacket(player);
            equipment.sendPacket(player);
            metadata.sendPacket(player);
        }
        return spawn.getEntityID();
    }

    private List<WrappedWatchableObject> fillMetadata(WrapperPlayServerEntityMetadata metadata) {

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher(metadata.getMetadata());
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x20 )); //invis
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(11, WrappedDataWatcher.Registry.get(Byte.class)), (byte) (0x01 | 0x08 | 0x10)); //isSmall, noBasePlate, set Marker

        return dataWatcher.getWatchableObjects();
    }

   // public void destroy(Player p) {
   //     WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
   //     destroy.setEntityIds(new int[]{ENTITY_ID});
   //     destroy.sendPacket(p);
   // }
}
