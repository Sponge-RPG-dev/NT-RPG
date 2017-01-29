/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.utils;

import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.ItemRestriction;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.spongepowered.api.item.ItemTypes.*;
import static org.spongepowered.api.item.ItemTypes.LEATHER_HELMET;

/**
 * Created by NeumimTo on 27.3.2015.
 */
public class ItemStackUtils {
    /*If you want to add custom type of sword/axe/armor... via mod or resourcepack(remodeled potatoes) put them into these collections */
    public static Set<ItemType> swords = new HashSet<ItemType>() {{
        add(DIAMOND_SWORD);
        add(GOLDEN_SWORD);
        add(IRON_SWORD);
        add(STONE_SWORD);
        add(WOODEN_SWORD);
    }};
    public static Set<ItemType> axes = new HashSet<ItemType>() {{
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
    public static Set<ItemType> bows = new HashSet<ItemType>() {{
        add(BOW);
    }};
    public static Set<ItemType> staffs = new HashSet<ItemType>() {{
        add(BLAZE_ROD);
        add(STICK);
    }};
    public static Set<ItemType> weapons = new HashSet<ItemType>() {{
        addAll(swords);
        addAll(axes);
        addAll(bows);
        addAll(pickaxes);
        addAll(hoes);
    }};
    public static Set<ItemType> consumables = new HashSet<ItemType>() {{
        addAll(Arrays.asList(APPLE,
                GOLDEN_APPLE,
                BAKED_POTATO,
                CARROT, POTION, BREAD, POTATO,
                POISONOUS_POTATO, ROTTEN_FLESH, PORKCHOP, COOKED_BEEF, COOKED_CHICKEN, COOKED_MUTTON,
                COOKIE, COOKED_RABBIT, COOKED_FISH, FISH, CHICKEN, MELON));
    }};

    public static Set<ItemType> boots = new HashSet<ItemType>() {{
        addAll(Arrays.asList(DIAMOND_BOOTS,GOLDEN_BOOTS,IRON_BOOTS,CHAINMAIL_BOOTS,LEATHER_BOOTS));
    }};

    public static Set<ItemType> chestplates = new HashSet<ItemType>() {{
        addAll(Arrays.asList(DIAMOND_CHESTPLATE,GOLDEN_CHESTPLATE,IRON_CHESTPLATE,CHAINMAIL_CHESTPLATE,LEATHER_CHESTPLATE));
    }};

    public static Set<ItemType> leggings = new HashSet<ItemType>() {{
        addAll(Arrays.asList(DIAMOND_LEGGINGS,GOLDEN_LEGGINGS,IRON_LEGGINGS,CHAINMAIL_LEGGINGS,LEATHER_LEGGINGS));
    }};

    public static Set<ItemType> helmet = new HashSet<ItemType>() {{
        addAll(Arrays.asList(DIAMOND_HELMET,GOLDEN_HELMET,IRON_HELMET,CHAINMAIL_HELMET,LEATHER_HELMET));
    }};

    public static Map<String, ItemRestriction> restrictionMap = new HashMap<>();

    protected static String ID = "id";
    protected static String QUANTITY = "quantity";
    protected static String DAMAGE = "damage";
    protected static String DISPLAY_NAME = "name";
    protected static String LORE = "lore";
    protected static GlobalScope globalScope = NtRpgPlugin.GlobalScope;
    private static BiFunction<String, String, String> formatedConfig = (k, v) -> Utils.newLine(k + ": " + v + ";");
    private static Pattern pattern = Pattern.compile("\\((.*?)\\)");

    public static boolean isSword(ItemType type) {
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

    public static boolean isStaff(ItemType type) {
        return staffs.contains(type);
    }

    public static boolean isHelmet(ItemType type) {
        return helmet.contains(type);
    }

    public static boolean isChestplate(ItemType type) {
        return chestplates.contains(type);
    }

    public static boolean isLeggings(ItemType type) {
        return leggings.contains(type);
    }

    public static boolean isBoots(ItemType type) {
        return boots.contains(type);
    }

    public static boolean isItemSkillBind(ItemStack is) {
        if (is.getItem() != InventoryService.ITEM_SKILL_BIND) {
            return false;
        }
        Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
        if (texts.isPresent()) {
            List<Text> a = texts.get();
            if (a.size() > 1) {
                Text text = a.get(0);
                if (text.toPlain().equalsIgnoreCase(Localization.SKILLBIND)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void createProperty(StringBuilder builder, String value, String key) {
        if (key != null)
            builder.append(value).append(":").append(key).append(";");
    }

    private static void createProperty(StringBuilder builder, String value, Text key) {
        if (key != null)
            createProperty(builder, value, key.toString());
    }

    private static void createProperty(StringBuilder b, String value, int key) {
        createProperty(b, value, String.valueOf(key));
    }

    private static Integer getLevel(String s, Map<String, Integer> levels) {
        Integer i = levels.get(s);
        return i == null ? 0 : i;
    }

    public static DisplayNameData setDisplayName(Text name) {
        final DisplayNameData itemName = Sponge.getGame().getDataManager().getManipulatorBuilder(DisplayNameData.class).get().create();
        itemName.set(Keys.DISPLAY_NAME, name);
        return itemName;
    }

    /**
     * Returns a collection of global ffects and its levels from itemlore
     *
     * @param is
     * @return
     */
    public static Map<IGlobalEffect, Integer> getItemEffects(ItemStack is) {
        Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
        if (texts.isPresent()) {
            return getItemEffects(texts.get());
        }
        return Collections.emptyMap();
    }

    /**
     * Builds a effect map from item lore.
     *
     * @param texts
     * @return Map<Effect,Level>
     */
    public static Map<IGlobalEffect, Integer> getItemEffects(List<Text> texts) {
        Map<IGlobalEffect, Integer> map = new HashMap<>();
        texts.stream().filter(t -> t.getFormat().getColor() == TextColors.BLUE).forEach(t -> {
            findItemEffect(t, map);
        });
        return map;
    }

    public static void findItemEffect(Text text, Map<IGlobalEffect, Integer> map) {
        String eff = text.toPlain().substring(3).toLowerCase();
        String[] arr = eff.split(": ");
        int level = Integer.parseInt(arr[1]);
        IGlobalEffect effect = globalScope.effectService.getGlobalEffect(arr[0]);
        if (effect != null) {
            map.put(effect, level);
        }
    }

    public static List<Text> addItemEffect(ItemStack itemStack, IGlobalEffect globalEffect, int level) {
        Optional<List<Text>> texts = itemStack.get(Keys.ITEM_LORE);
        List<Text> lore = null;
        if (texts.isPresent()) {
            lore = texts.get();
        } else {
            lore = new ArrayList<>();
        }
        lore.add(Text.of(TextColors.AQUA, globalEffect.getName() + ": " + level));
        return lore;
    }

    public static List<Text> addItemEffect(ItemStack itemStack, IGlobalEffect globalEffect, float level) {
        Optional<List<Text>> texts = itemStack.get(Keys.ITEM_LORE);
        List<Text> lore = null;
        if (texts.isPresent()) {
            lore = texts.get();
        } else {
            lore = new ArrayList<>();
        }
        lore.add(Text.of(TextColors.AQUA, globalEffect.getName() + ": " + level));
        return lore;
    }

    public static boolean isConsumable(ItemType type) {
        return consumables.contains(type);
    }

    public static boolean isItemRune(ItemStack is) {
        Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
        if (texts.isPresent()) {
            List<Text> a = texts.get();
            if (a.size() >= 1) {
                Text text = a.get(0);
                String s = text.toPlain();
                if (s.equalsIgnoreCase(Localization.RUNE)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasSockets(ItemStack itemStack) {
        return globalScope.runewordService.getSocketCount(itemStack.get(Keys.ITEM_LORE).get()) > 0;
    }

    /**
     * https://github.com/SpongePowered/SpongeForge/issues/470
     *
     * @param itemStack
     */
    public static void createEnchantmentGlow(ItemStack itemStack) {
        itemStack.offer(Sponge.getDataManager().getManipulatorBuilder(EnchantmentData.class).get().create());
    }

    public static boolean isCharm(ItemStack is) {
        Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
        if (texts.isPresent()) {
            List<Text> texts1 = texts.get();
            if (texts1.size() > 1) {
                String s = texts1.get(1).toPlain();
                if (s.equalsIgnoreCase(Localization.CHARM)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getItemLevel(Text text) {
        String s = text.toPlain();
        String[] split = s.split(":");
        if (split.length > 1) {
            return Integer.parseInt(split[1]);
        }
        return 0;
    }

    public static Set<String> getRestrictions(Text text) {
        Set<String> str = new HashSet<>();
        Matcher m = pattern.matcher(text.toPlain());

        return str;
    }

    public static void dropItem(Player player, ItemStack itemStack) {
        Entity optional = player.getLocation().getExtent().createEntity(EntityTypes.ITEM, player.getLocation().getPosition());
        Item item = (Item) optional;
            item.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
            player.getLocation().getExtent().spawnEntity(item, Cause.of(NamedCause.of("player", player)));

    }

    static {
        restrictionMap.put("L", ItemRestriction.Level);
        restrictionMap.put("G", ItemRestriction.Group);
    }

    public static boolean checkType(ItemType i, String item) {
        if (item.equalsIgnoreCase("sword")){
            return swords.contains(i);
        }
        if (item.equalsIgnoreCase("axe")){
            return axes.contains(i);
        }
        if (item.equalsIgnoreCase("pickaxe")) {
            return pickaxes.contains(i);
        }
        if (item.equalsIgnoreCase("hoe")){
            return hoes.contains(i);
        }
        if (item.equalsIgnoreCase("staff")) {
            return staffs.contains(i);
        }
        return false;
    }

    public static ItemStack createHelpItem(String description, String name) {
        String[] split = description.split("\n");
        List<Text> descr = new ArrayList<>();
        for (String s : split) {
            descr.add(Text.of(s,TextColors.WHITE));
        }
        return ItemStack.builder().itemType(ItemTypes.PAPER)
                .quantity(1)
                .keyValue(Keys.ITEM_LORE, descr)
                .keyValue(Keys.DISPLAY_NAME, Text.of(name))
                .build();

    }
}
