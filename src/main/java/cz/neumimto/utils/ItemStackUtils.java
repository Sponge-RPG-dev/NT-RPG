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

package cz.neumimto.utils;

import com.typesafe.config.Config;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.configuration.Localization;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.inventory.InventoryService;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.skills.SkillData;
import cz.neumimto.skills.SkillItemIcon;
import cz.neumimto.skills.SkillSettings;
import cz.neumimto.skills.SkillTree;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.spongepowered.api.item.ItemTypes.*;

/**
 * Created by NeumimTo on 27.3.2015.
 */
public class ItemStackUtils {
    protected static String ID = "id";
    protected static String QUANTITY = "quantity";
    protected static String DAMAGE = "damage";
    protected static String DISPLAY_NAME = "name";
    protected static String LORE = "lore";
    protected static cz.neumimto.GlobalScope globalScope = NtRpgPlugin.GlobalScope;

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

    public static ItemStack fromConfig(Config c) {
        String type = c.getString(ID);
        int amount = c.getInt(QUANTITY);
        int damage = c.getInt(DAMAGE);
        String name = c.getString(DISPLAY_NAME);
        List<Text> stringList = new ArrayList<>();
        c.getStringList(LORE).stream().forEach(s -> stringList.add(Text.of(s)));
        Optional<ItemType> asd = globalScope.game.getRegistry().getType(ItemType.class, type);
        if (asd.isPresent()) {
            ItemStack item = ItemStack.builder().itemType(asd.get()).build();
            item.setQuantity(amount);
            item.offer(Keys.ITEM_LORE,stringList);
            item.offer(Keys.DISPLAY_NAME,Text.of(name));
            item.offer(Keys.ITEM_DURABILITY,damage);
            return item;
        }
        throw new RuntimeException("Non existing item type " + type);
    }

    private static BiFunction<String,String,String> formatedConfig = (k,v) -> Utils.newLine(k+": "+v+";");

    public static String itemStackToFormatedConfig(ItemStack itemStack) {

        String s = "{"+Utils.LineSeparator;
        s += formatedConfig.apply(ID,itemStack.getItem().getId());
        s += formatedConfig.apply(QUANTITY, String.valueOf(itemStack.getQuantity()));
        s += formatedConfig.apply(DAMAGE, String.valueOf(itemStack.get(Keys.ITEM_DURABILITY).get()));
        s += formatedConfig.apply(DISPLAY_NAME, itemStack.get(Keys.DISPLAY_NAME).get().toPlain());
        s += "}";
        return s;
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

    public static ItemStack skillToItemStack(SkillItemIcon icon, NClass nClass, CharacterBase character) {
        Map<String, Integer> skills = character.getSkills();
        final Integer level = getLevel(icon.skillName, skills);
        TextColor skillnamecolor = null;
        if (level == 0) {
            skillnamecolor = TextColors.GREEN;
        } else {
            skillnamecolor = TextColors.RED;
        }
        final LoreData loreData = Sponge.getGame().getDataManager().getManipulatorBuilder(LoreData.class).get().create();
        final ListValue<Text> locallore = loreData.lore();
        SkillTree skillTree = nClass.getSkillTree();
        SkillData skillData = skillTree.getSkills().get(icon.skillName);
        //skilltree settings info
        locallore.add(Text.of(icon.skill.getDescription()));
        locallore.add(Text.of(""));
        locallore.add(Text.of(Localization.LORESECTION_MAX_SKILL_LEVEL + ": " + skillData.getMaxSkillLevel()));
        locallore.add(Text.of(Localization.LORESECTION_MAX_PLAYER_LEVEL + ": " + skillData.getMinPlayerLevel()));
        //skillsettings
        locallore.add(Text.of(Localization.SKILL_SETTINGS_LORESECTION_NAME));
        SkillSettings skillSettings = skillData.getSkillSettings();
        final int roundprecision = 1;
        skillSettings.getNodes().keySet().stream()
                .filter(s -> !s.endsWith(SkillSettings.bonus))
                .forEach(n -> {
                    locallore.add(Text.of(
                            "- " + n + ": " + Utils.round(skillData.getSkillSettings().getNodeValue(n), roundprecision)
                                    + " | " + Utils.round(skillData.getSkillSettings().getLevelNodeValue(n, level), roundprecision)
                                    + " | " + Utils.round(skillData.getSkillSettings().getNodeValue(n + SkillSettings.bonus), roundprecision)));

                });
        //skill dependencies
        locallore.add(Text.of(""));
        String strlist = skillData.getConflicts()
                .stream()
                .map(SkillData::getSkillName)
                .collect(Collectors.joining(", "));
        locallore.add(Text.of(Localization.LORESECTION_CONFCLICTS + ": " + strlist));
        strlist = skillData.getSoftDepends()
                .stream()
                .map(SkillData::getSkillName)
                .collect(Collectors.joining(", "));
        locallore.add(Text.of(Localization.LORESECTION_SOFT_DEPENDS + ": " + strlist));
        strlist = skillData.getHardDepends()
                .stream()
                .map(SkillData::getSkillName)
                .collect(Collectors.joining(", "));
        locallore.add(Text.of(Localization.LORESECTION_HARD_DEPENDS + ": " + strlist));
        //skill lore
        if (icon.skill.getLore() != null) {
            locallore.add(Text.of(""));
            locallore.add(Text.of(icon.skill.getLore()));
        }

        final DisplayNameData itemName = Sponge.getGame().getDataManager().getManipulatorBuilder(DisplayNameData.class).get().create();
        itemName.set(Keys.DISPLAY_NAME, Text.of(skillnamecolor, icon.skillName));
        // Set up the lore data.

        ItemStack.Builder i = ItemStack.builder();
        return i.itemType(icon.itemType).itemData(itemName).itemData(loreData).quantity(level).build();
    }
    /**
     * Returns a collection global effects and its levels from itemlore
     * @param is
     * @return
     */

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
     * @param texts
     * @return Map<Effect,Level>
     */
    public static Map<IGlobalEffect, Integer> getItemEffects(List<Text> texts) {
        Map<IGlobalEffect, Integer> map = new HashMap<>();
        texts.stream().filter(t -> t.getFormat().getColor() == TextColors.BLUE).forEach(t -> {
            findItemEffect(t,map);
        });
        return map;
    }

    public static void findItemEffect(Text text,Map<IGlobalEffect,Integer> map) {
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
        lore.add(Text.of(TextColors.AQUA,globalEffect.getName()+":" + level));
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
        lore.add(Text.of(TextColors.AQUA,globalEffect.getName()+":" + level));
        return lore;
    }

    public static Set<ItemType> consumables = new HashSet<ItemType>() {{
        addAll(Arrays.asList(APPLE,
                GOLDEN_APPLE,
                BAKED_POTATO,
                CARROT, POTION, BREAD, POTATO,
                POISONOUS_POTATO, ROTTEN_FLESH, PORKCHOP, COOKED_BEEF, COOKED_CHICKEN, COOKED_MUTTON,
                COOKIE, COOKED_RABBIT, COOKED_FISH, FISH, CHICKEN,MELON));
    }};

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

    private static Pattern pattern = Pattern.compile("\\((.*?)\\)");
    public static Set<String> getRestrictions(Text text) {
        Set<String> str = new HashSet<>();
        Matcher m = pattern.matcher(text.toPlain());

        return str;
    }
}
