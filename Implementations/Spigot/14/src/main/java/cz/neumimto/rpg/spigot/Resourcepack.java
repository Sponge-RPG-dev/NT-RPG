package cz.neumimto.rpg.spigot;

import cz.neumimto.rpg.api.utils.rng.XORShiftRnd;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Resourcepack {

    private static XORShiftRnd rnd;
    static {
        rnd = new XORShiftRnd();
    }

    private static final int ICE_SPIKE_LARGE = 12349;

    public static Entity summonLargeIceSpike(Location location) {
        World world = location.getWorld();
        ArmorStand entity = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        entity.setArms(true);
        entity.setVisible(false);
        entity.setCollidable(false);
        entity.setInvulnerable(false);


        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(ICE_SPIKE_LARGE);
        itemStack.setItemMeta(itemMeta);
        entity.setHelmet(itemStack);
        world.playSound(location, Sound.BLOCK_GLASS_BREAK, 1F, 0.5F);

        return entity;
    }
}
