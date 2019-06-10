package cz.neumimto.rpg.sponge.gui;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.inventory.sockets.SocketType;
import cz.neumimto.rpg.common.reloading.Reload;
import cz.neumimto.rpg.common.reloading.ReloadService;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.inventory.ItemLoreSections;
import cz.neumimto.rpg.sponge.inventory.LoreSectionDelimiter;
import cz.neumimto.rpg.sponge.inventory.data.DataConstants;
import cz.neumimto.rpg.sponge.inventory.data.NKeys;
import cz.neumimto.rpg.sponge.inventory.data.manipulators.ItemSocketsData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.sponge.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 20.1.2018.
 */
@Singleton
@ResourceLoader.ListenerClass
public class ItemLoreBuilderService {

    private static TextColor effectName;
    private static TextColor doubleColon;
    private static TextColor value;
    private static TextColor effectSettings;
    private static TextColor groupMinLevelColor;
    private static Text effectSection;
    private static Text rarity;
    private static Text damage;
    private static Text level;
    private static Text sockets;
    private static Text attributes;
    private static Text requirements;
    private static List<ItemLoreSections> loreOrder;
    private static Map<Integer, Text> rarityMap = new HashMap<>();
    private static Text unknownRarity;
    private static Text metaType;

    public static ItemLoreBuilder create(ItemStack is, List<Text> t) {
        return new ItemLoreBuilder(is, t);
    }

    @Listener
    public void pluginInit(GameStartedServerEvent e) {
        setupColor();
    }

    @Reload(on = ReloadService.PLUGIN_CONFIG)
    public void setupColor() {
        effectName = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_EFFECT_NAME_COLOR).get();
        doubleColon = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_EFFECT_COLON_COLOR).get();
        value = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_EFFECT_VALUE_COLOR).get();
        effectSettings = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_EFFECT_SECTION_COLOR).get();
        groupMinLevelColor = Sponge.getRegistry().getType(TextColor.class, pluginConfig.ITEM_LORE_GROUP_MIN_LEVEL_COLOR).get();


        effectSection = TextHelper.parse(LocalizationKeys.ITEM_EFFECTS_SECTION);
        rarity = TextHelper.parse(LocalizationKeys.ITEM_RARITY_SECTION);
        damage = TextHelper.parse(LocalizationKeys.ITEM_DAMAGE_SECTION);
        level = TextHelper.parse(LocalizationKeys.ITEM_LEVEL_SECTION);
        sockets = TextHelper.parse(LocalizationKeys.ITEM_SOCKETS_SECTION);
        attributes = TextHelper.parse(LocalizationKeys.ITEM_ATTRIBUTES_SECTIO);
        requirements = TextHelper.parse(LocalizationKeys.ITEM_REQUIREMENTS_SECTION);
        metaType = TextHelper.parse(LocalizationKeys.ITEM_META_TYPE_NAME);

        loreOrder = pluginConfig.ITEM_LORE_ORDER.stream().map(ItemLoreSections::valueOf).collect(Collectors.toList());

        for (String s : pluginConfig.ITEM_RARITY) {
            String[] split = s.split(",");
            Integer i = Integer.parseInt(split[0]);
            Text t = TextHelper.parse(split[1]);
            rarityMap.put(i, t);
        }

        unknownRarity = TextHelper.parse(LocalizationKeys.UNKNOWN_RARITY);

    }

    public static class ItemLoreBuilder {

        private final ItemStack is;
        private final List<Text> t;

        private ItemLoreBuilder(ItemStack is, List<Text> t) {
            this.is = is;
            this.t = t;
        }

        public void createAttributesSection() {
            is.get(NKeys.ITEM_ATTRIBUTE_BONUS).ifPresent(a -> {
                if (a.isEmpty()) {
                    return;
                }
                createDelimiter(attributes);
                attributeMapToItemLorePart(a);
            });
        }


        public void attributeMapToItemLorePart(Map<String, Integer> a) {
            int k = 0;
            PropertyService propertyService = Rpg.get().getPropertyService();
            for (Map.Entry<String, Integer> e : a.entrySet()) {
                AttributeConfig attribute = propertyService.getAttributeById(e.getKey()).get();
                String name = attribute.getName();
                int charsToRead = 3;
                if (name.startsWith("&")) {
                    charsToRead = 5;
                }
                Text q = null;
                if (name.length() > charsToRead) {
                    q = TextHelper.parse(name.substring(0, charsToRead) + ": ");
                } else {
                    q = TextHelper.parse(name + ": ");
                }

                t.add(Text.builder()
                        .append(q)
                        .append(Text.builder(e.getValue() + " ").color(TextColors.WHITE).build())
                        .build());
                k++;
                if (k % 3 == 0) {
                    t.add(Text.EMPTY);
                }
            }
        }

        public void createItemSocketsSection() {
            is.get(ItemSocketsData.class).ifPresent(a -> {
                List<SocketType> sockets = a.getSockets();
                List<Text> content = a.getContent();
                createDelimiter(ItemLoreBuilderService.sockets);
                for (int i = 0; i < sockets.size(); i++) {
                    if (DataConstants.EMPTY_SOCKET.equals(content.get(i))) {
                        String msg = Rpg.get().getLocalizationService().translate(LocalizationKeys.SOCKET_EMPTY, Arg.arg("socket", sockets.get(i).getName()));
                        t.add(TextHelper.parse(msg));
                    } else {
                        t.add(Text.builder("- ").color(TextColors.DARK_RED).append(content).build());
                    }
                }
            });
        }

        public void createItemMetaSection() {
            is.get(NKeys.ITEM_META_HEADER).ifPresent(a -> {
                createDelimiter(a);
                is.get(NKeys.ITEM_RARITY).ifPresent(r -> {
                    t.add(Text.builder().append(rarity).append(translateRarity(r)).append(Text.NEW_LINE).build());
                });
                is.get(NKeys.ITEM_DAMAGE).ifPresent(r -> {
                    if (r.max == 0) {
                        t.add(Text.builder()
                                .append(damage)
                                .append(Text.builder(String.valueOf(r.min))
                                        .color(NtRpgPlugin.GlobalScope.damageService.getColorByDamage(r.min))
                                        .build())
                                .append(Text.NEW_LINE)
                                .build());
                    } else {
                        t.add(Text.builder()
                                .append(damage)
                                .append(Text.builder(String.valueOf(r.min))
                                        .color(NtRpgPlugin.GlobalScope.damageService.getColorByDamage(r.min))
                                        .build())
                                .append(Text.builder(" - ").color(TextColors.GRAY).build())
                                .append(Text.builder(String.valueOf(r.max))
                                        .color(NtRpgPlugin.GlobalScope.damageService.getColorByDamage(r.max))
                                        .build())
                                .append(Text.NEW_LINE).build());
                    }
                });
                is.get(NKeys.ITEM_META_TYPE).ifPresent(r -> {
                    t.add(Text.builder()
                            .append(metaType)
                            .append(
                                    Text.builder(r.getName())
                                            .color(TextColors.GOLD)
                                            .build()
                            )
                            .build());
                });
                is.get(NKeys.ITEM_LEVEL).ifPresent(r -> {
                    t.add(Text.builder().append(level)
                            .append(Text.builder(String.valueOf(r))
                                    .color(TextColors.YELLOW)
                                    .build())
                            .append(Text.NEW_LINE)
                            .build());
                });
            });
        }

        private void createRequirements() {
            Optional<Map<String, Integer>> groups = is.get(NKeys.ITEM_PLAYER_ALLOWED_GROUPS);
            Optional<Map<String, Integer>> attr = is.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS);
            if (groups.isPresent() || attr.isPresent()) {
                createDelimiter(requirements);
                groupsToItemLore(groups.orElse(Collections.emptyMap()));
                attributeMapToItemLorePart(attr.orElse(Collections.emptyMap()));
            }
        }

        private void groupsToItemLore(Map<String, Integer> r) {
            for (Map.Entry<String, Integer> stringIntegerEntry : r.entrySet()) {
                Integer value = stringIntegerEntry.getValue();
                String group = stringIntegerEntry.getKey();
                ClassDefinition byName = NtRpgPlugin.GlobalScope.classService.getClassDefinitionByName(group);
                if (byName != null) {
                    if (value > 0) {
                        t.add(
                                Text.builder("* ")
                                        .color(TextColors.DARK_RED)
                                        .style(TextStyles.BOLD)
                                        .append(Text.builder(byName.getName())
                                                .color(toTextColor(byName.getPreferedColor()))
                                                .append(Text.builder(": ").color(TextColors.GRAY).style(TextStyles.BOLD).build())
                                                .append(Text.builder(String.valueOf(value)).color(groupMinLevelColor).build())
                                                .build())
                                        .build());
                    } else {
                        t.add(
                                Text.builder("* ")
                                        .color(TextColors.DARK_RED)
                                        .style(TextStyles.BOLD)
                                        .append(Text.builder(byName.getName())
                                                .color(toTextColor(byName.getPreferedColor()))
                                                .build())
                                        .build());
                    }
                }
            }
        }

        private TextColor toTextColor(String id) {
            return Sponge.getRegistry().getType(TextColor.class, id).orElseGet(() -> {
                Log.warn("unknown text color " + id);
                return TextColors.WHITE;
            });
        }

        private Text translateRarity(Integer r) {
            Text text = rarityMap.get(r);
            if (text == null) {
                text = unknownRarity;
            }
            return text;
        }

        public void createDelimiter(Text section) {
            LoreSectionDelimiter loreSectionDelimiter = is.get(NKeys.ITEM_SECTION_DELIMITER)
                    .orElse(new LoreSectionDelimiter(LoreSectionDelimiter.defaultFirstPart, LoreSectionDelimiter.defaultSecondPart));
            t.add(Text.builder().append(loreSectionDelimiter.firstPart).append(section).append(loreSectionDelimiter.secondPart).build());
        }

        public void createEffectsSection() {
            is.get(NKeys.ITEM_EFFECTS).ifPresent(a -> {
                createDelimiter(effectSection);
                itemEffectsToTextList(a);
            });
        }

        public void itemEffectsToTextList(Map<String, EffectParams> a) {
            for (Map.Entry<String, EffectParams> entry : a.entrySet()) {
                if (entry.getValue() == null) {
                    t.add(Text.builder(entry.getKey()).color(effectName).append(Text.NEW_LINE).build());
                } else if (entry.getValue().size() == 1
                        && entry.getKey()
                        .equalsIgnoreCase(entry.getValue().entrySet().stream().findFirst().get().getKey())) {
                    t.add(Text.builder(entry.getKey()).color(effectName)
                            .append(Text.builder(": ").color(doubleColon).build())
                            .append(Text.builder(entry.getValue().get(entry.getKey())).color(value).build())
                            .build());
                } else {
                    t.add(Text.builder(entry.getKey()).color(effectName).build());
                    for (Map.Entry<String, String> q : entry.getValue().entrySet()) {
                        t.add(Text.builder("  - " + q.getKey()).color(effectSettings)
                                .append(Text.builder(": ").color(doubleColon).build())
                                .append(Text.builder(q.getValue()).color(value).build())
                                .build());
                    }
                }
            }
        }


        public List<Text> buildLore() {
            for (ItemLoreSections itemLoreSections : loreOrder) {
                switch (itemLoreSections) {
                    case META:
                        createItemMetaSection();
                        break;
                    case EFFECTS:
                        createEffectsSection();
                        break;
                    case SOCKETS:
                        createItemSocketsSection();
                        break;
                    case ATTRIBUTES:
                        createAttributesSection();
                        break;
                    case REQUIREMENTS:
                        createRequirements();
                        break;
                }
            }
            return t;
        }
    }
}
