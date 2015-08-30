package cz.neumimto.utils;

import com.typesafe.config.Config;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import static org.spongepowered.api.item.ItemTypes.*;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import static cz.neumimto.NtRpgPlugin.GlobalScope;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by NeumimTo on 27.3.2015.
 */
public class ItemStackUtils {
    protected static String ID = "id";
    protected static String COUNT = "amount";
    protected static String DAMAGE = "damage";
    protected static String DISPLAY_NAME = "name";
    protected static String LORE = "lore";

    /*If you want to add custom type of sword/axe/armor... via mod or resourcepack(remodeled potatoes) put them into these collections */
    public static Set<ItemType> swords = new HashSet<ItemType>() {{
        add(DIAMOND_SWORD);
        add(GOLDEN_SWORD);
        add(IRON_SWORD);
        add(STONE_SWORD);
        add(WOODEN_SWORD);
    }};

    public static Set<ItemType> axes = new HashSet<ItemType>(){{
        add(DIAMOND_AXE);
        add(GOLDEN_AXE);
        add(IRON_AXE);
        add(STONE_AXE);
        add(WOODEN_AXE);
    }};

    public static Set<ItemType> pickaxes = new HashSet<ItemType>() {{
        add(DIAMOND_PICKAXE);
        add(GOLDEN_PICKAXE);
        add(IRON_PICKAXE);
        add(STONE_PICKAXE);
        add(WOODEN_PICKAXE);
    }};

    public static Set<ItemType> hoes = new HashSet<ItemType>() {{
        add(DIAMOND_HOE);
        add(GOLDEN_HOE);
        add(IRON_HOE);
        add(STONE_HOE);
        add(WOODEN_HOE);
    }};

    public static Set<ItemType> bows = new HashSet<ItemType>(){{
        add(BOW);
    }};

    public static Set<ItemType> weapons = new HashSet<ItemType>() {{
        addAll(swords);
        addAll(axes);
        addAll(bows);
        addAll(pickaxes);
        addAll(hoes);
    }};

    public static boolean isSword(ItemType type){
        return swords.contains(type);
    }

    public static boolean isAxe(ItemType type) {
        return axes.contains(type);
    }

    public static boolean isPickaxe(ItemType type) {
        return pickaxes.contains(type);
    }

    public static boolean isHoe(ItemType type) {
        return hoes.contains(type);
    }

    public static boolean isBow(ItemType type) {
        return bows.contains(type);
    }

    public static boolean isWeapon(ItemType type) {
        return weapons.contains(type);
    }

    public static ItemStack fromConfig(Config c) {
        ItemType type = null; /* GlobalScope.game.getRegistry().getType(ItemType.class, c.getString("type")); */
        int amount = c.getInt(COUNT);
        int damage = c.getInt(DAMAGE);
        return GlobalScope.game.getRegistry().createItemBuilder().itemType(type).quantity(amount).build();
    }

    public static void loadItemLore(org.spongepowered.api.item.inventory.ItemStack o, List<String> lore) {

    }

    public static String itemStackToString(ItemStack item) {
        StringBuilder builder = new StringBuilder();
        createProperty(builder, ID, item.getItem().getId());
        createProperty(builder, COUNT, item.getQuantity());
        createProperty(builder, DISPLAY_NAME, item.get(Keys.DISPLAY_NAME).get());
        return builder.toString();
    }

    public static ItemStack StringToItemStack(String str) {
        return null;
    }

    private static void createProperty(StringBuilder builder, String value, String key) {
        if (key != null)
            builder.append(value).append(key).append(";");
    }

    private static void createProperty(StringBuilder builder, String value, Text key) {
        if (key != null)
           createProperty(builder,value,key.toString());
    }

    private static void createProperty(StringBuilder b, String value, int key) {
        createProperty(b, value, String.valueOf(key));
    }
}
