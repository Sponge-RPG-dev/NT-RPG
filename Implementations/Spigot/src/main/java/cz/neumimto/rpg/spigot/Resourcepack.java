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

import java.util.concurrent.ThreadLocalRandom;

public class Resourcepack {

    private static XORShiftRnd rnd;

    static {
        rnd = new XORShiftRnd();
    }

    public static RPItem BACK = new RPItem(Material.PAPER, 12345);
    public static RPItem CONFIRM = new RPItem(Material.DIAMOND, 12345);

    public static RPItem ARMOR = new RPItem(Material.DIAMOND_CHESTPLATE, 12345);
    public static RPItem WEAPONS = new RPItem(Material.DIAMOND_SWORD, 12345);

    public static RPItem ATTRIBUTES = new RPItem(Material.BOOK, 12345);

    public static RPItem PLUS = new RPItem(Material.GREEN_DYE, 12345);
    public static RPItem MINUS = new RPItem(Material.DIAMOND_SWORD, 12345);

    public static RPItem UP = new RPItem(Material.STICK, 12345);
    public static RPItem DOWN = new RPItem(Material.STICK, 12346);
    public static RPItem LEFT = new RPItem(Material.STICK, 12347);
    public static RPItem RIGHT = new RPItem(Material.STICK, 12348);
    public static RPItem SKILLTREE = new RPItem(Material.OAK_SAPLING, 12345);

    private static final int FIRE_REMNANT = 12344;
    private static final int ICE_SPIKE_LARGE = 12349;

    public static ItemStack SLASH_01;

    public static ItemStack ICICLE_01;
    public static ItemStack ICICLE_02;
    public static ItemStack ICICLE_03;
    public static ItemStack ICICLE_04;
    public static ItemStack ICICLE_05;
    public static ItemStack ICICLE_06;
    public static ItemStack ICICLE_07;
    private static ItemStack[] icicles;

    public static void init() {
        SLASH_01 = tag(Material.LAPIS_LAZULI, 2457);

        ICICLE_01 = tag(Material.STICK, 12350);
        ICICLE_02 = tag(Material.STICK, 12351);
        ICICLE_03 = tag(Material.STICK, 12352);
        ICICLE_04 = tag(Material.STICK, 12353);
        ICICLE_05 = tag(Material.STICK, 12354);
        ICICLE_06 = tag(Material.STICK, 12355);
        ICICLE_07 = tag(Material.STICK, 12356);

        icicles = new ItemStack[]{ICICLE_01, ICICLE_02, ICICLE_03, ICICLE_04, ICICLE_05, ICICLE_06, ICICLE_07};
    }

    public static ItemStack randomIcicle() {
        int rnd = ThreadLocalRandom.current().nextInt(icicles.length);
        return icicles[rnd];
    }

    private static ItemStack tag(Material material, int tag) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(tag);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public static ArmorStand summonArmorStand(Location location) {
        World world = location.getWorld();
        ArmorStand entity = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        entity.setArms(true);
        entity.setVisible(false);
        entity.setCollidable(false);
        entity.setInvulnerable(true);
        return entity;
    }

    public static Entity summonLargeIceSpike(Location location) {
        World world = location.getWorld();
        ArmorStand entity = summonArmorStand(location);

        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(ICE_SPIKE_LARGE);
        itemStack.setItemMeta(itemMeta);
        entity.setHelmet(itemStack);
        world.playSound(location, Sound.BLOCK_GLASS_BREAK, 1F, 0.5F);

        return entity;
    }

    public static ArmorStand fireRemnant(Location location) {
        World world = location.getWorld();
        ArmorStand entity = summonArmorStand(location);
        entity.setInvisible(true);
        ItemStack itemStack = new ItemStack(Material.STICK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(FIRE_REMNANT);
        itemStack.setItemMeta(itemMeta);
        entity.getEquipment().setHelmet(itemStack);
        world.playSound(location, Sound.BLOCK_GLASS_BREAK, 1F, 0.5F);

        return entity;
    }

    public static class RPItem {
        public final Material mat;
        public final int model;

        public RPItem(Material material, int modelId) {
            this.mat = material;
            this.model = modelId;
        }
    }
}
