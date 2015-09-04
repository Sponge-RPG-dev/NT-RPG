package cz.neumimto.utils;

import com.typesafe.config.Config;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.Weapon;
import cz.neumimto.configuration.Localization;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.skills.SkillInfo;
import cz.neumimto.skills.SkillItemIcon;
import cz.neumimto.skills.SkillSettings;
import cz.neumimto.skills.SkillTree;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.NtRpgPlugin.GlobalScope;
import static org.spongepowered.api.item.ItemTypes.*;

/**
 * Created by NeumimTo on 27.3.2015.
 */
public class ItemStackUtils {
    protected static String ID = "id";
    protected static String COUNT = "amount";
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
        final DisplayNameData itemName = NtRpgPlugin.GlobalScope.game.getRegistry().getManipulatorRegistry().getBuilder(DisplayNameData.class).get().create();
        itemName.set(Keys.DISPLAY_NAME, name);
        return itemName;
    }

    public static LoreData setLore(Text... texts) {
        Game game = NtRpgPlugin.GlobalScope.game;
        final LoreData loreData = game.getRegistry().getManipulatorRegistry().getBuilder(LoreData.class).get().create();
        final ListValue<Text> locallore = loreData.lore();
        for (Text t : texts)
            locallore.add(t);
        return loreData;
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
        Game game = NtRpgPlugin.GlobalScope.game;
        final LoreData loreData = game.getRegistry().getManipulatorRegistry().getBuilder(LoreData.class).get().create();
        final ListValue<Text> locallore = loreData.lore();
        SkillTree skillTree = nClass.getSkillTree();
        SkillInfo skillInfo = skillTree.getSkills().get(icon.skillName);
        //skilltree settings info
        locallore.add(Texts.of(icon.skill.getDescription()));
        locallore.add(Texts.of(""));
        locallore.add(Texts.of(Localization.LORESECTION_MAX_SKILL_LEVEL + ": " + skillInfo.getMaxSkillLevel()));
        locallore.add(Texts.of(Localization.LORESECTION_MAX_PLAYER_LEVEL + ": " + skillInfo.getMinPlayerLevel()));
        //skillsettings
        locallore.add(Texts.of(Localization.SKILL_SETTINGS_LORESECTION_NAME));
        SkillSettings skillSettings = skillInfo.getSkillSettings();
        final int roundprecision = 1;
        skillSettings.getNodes().keySet().stream()
                .filter(s -> !s.endsWith(SkillSettings.bonus))
                .forEach(n -> {
                    locallore.add(Texts.of(
                            "- " + n + ": " + Utils.round(skillInfo.getSkillSettings().getNodeValue(n), roundprecision)
                                    + " | " + Utils.round(skillInfo.getSkillSettings().getLevelNodeValue(n, level), roundprecision)
                                    + " | " + Utils.round(skillInfo.getSkillSettings().getNodeValue(n + SkillSettings.bonus), roundprecision)));

                });
        //skill dependencies
        locallore.add(Texts.of(""));
        String strlist = skillInfo.getConflicts()
                .stream()
                .map(SkillInfo::getSkillName)
                .collect(Collectors.joining(", "));
        locallore.add(Texts.of(Localization.LORESECTION_CONFCLICTS + ": " + strlist));
        strlist = skillInfo.getSoftDepends()
                .stream()
                .map(SkillInfo::getSkillName)
                .collect(Collectors.joining(", "));
        locallore.add(Texts.of(Localization.LORESECTION_SOFT_DEPENDS + ": " + strlist));
        strlist = skillInfo.getHardDepends()
                .stream()
                .map(SkillInfo::getSkillName)
                .collect(Collectors.joining(", "));
        locallore.add(Texts.of(Localization.LORESECTION_HARD_DEPENDS + ": " + strlist));
        //skill lore
        if (icon.skill.getLore() != null) {
            locallore.add(Texts.of(""));
            locallore.add(Texts.of(icon.skill.getLore()));
        }

        final DisplayNameData itemName = game.getRegistry().getManipulatorRegistry().getBuilder(DisplayNameData.class).get().create();
        itemName.set(Keys.DISPLAY_NAME, Texts.of(skillnamecolor, icon.skillName));
        // Set up the lore data.

        ItemStackBuilder i = game.getRegistry().createItemBuilder();
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
        List<Text> texts = is.get(Keys.ITEM_LORE).get();
        Map<IGlobalEffect, Integer> map = new HashMap<>();
        texts.stream().filter(t -> t.getFormat().getColor() == TextColors.AQUA).forEach(t -> {
            String eff = Texts.toPlain(t).substring(3).toLowerCase();
            String[] arr = eff.split(":");
            int level = Integer.parseInt(arr[1]);
            IGlobalEffect effect = globalScope.effectService.getGlobalEffect(arr[0]);
            if (effect != null) {
                map.put(effect, level);
            }
        });
        return map;
    }

    public static Weapon itemStackToWeapon(ItemStack itemStack) {
        Map<IGlobalEffect, Integer> itemEffects = getItemEffects(itemStack);
        Weapon weapon = new Weapon(itemStack.getItem());
        weapon.setEffects(itemEffects);
        return weapon;
    }
}
