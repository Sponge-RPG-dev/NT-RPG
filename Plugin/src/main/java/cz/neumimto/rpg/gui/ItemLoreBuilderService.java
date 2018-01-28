package cz.neumimto.rpg.gui;

import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.TextHelper;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.inventory.ItemLoreSections;
import cz.neumimto.rpg.inventory.LoreSectionDelimiter;
import cz.neumimto.rpg.inventory.SocketType;
import cz.neumimto.rpg.inventory.data.ItemSocket;
import cz.neumimto.rpg.inventory.data.NKeys;
import cz.neumimto.rpg.players.properties.attributes.ICharacterAttribute;
import cz.neumimto.rpg.reloading.Reload;
import cz.neumimto.rpg.reloading.ReloadService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 20.1.2018.
 */
@Singleton
public class ItemLoreBuilderService {

    private static TextColor effectName;
    private static TextColor doubleColon;
    private static TextColor value;
    private static TextColor effectSettings;
    private static Text effectSection;
    private static Text rarity;
    private static Text damage;
    private static Text level;
    private static Text sockets;
    private static Text socketcolon;
    private static Text attributes;
    private static List<ItemLoreSections> loreOrder;



    @PostProcess(priority = 3000)
    @Reload(on = ReloadService.PLUGIN_CONFIG)
    public void setupColor() {
        effectName = Sponge.getRegistry().getType(TextColor.class, PluginConfig.ITEM_LORE_EFFECT_NAME_COLOR).get();
        doubleColon = Sponge.getRegistry().getType(TextColor.class, PluginConfig.ITEM_LORE_EFFECT_COLON_COLOR).get();
        value = Sponge.getRegistry().getType(TextColor.class, PluginConfig.ITEM_LORE_EFFECT_VALUE_COLOR).get();
        effectSettings = Sponge.getRegistry().getType(TextColor.class, PluginConfig.ITEM_LORE_EFFECT_SETTING_NAME_COLOR).get();

        effectSection = TextHelper.parse(Localization.ITEM_EFFECTS_SECTION);
        rarity = TextHelper.parse(Localization.ITEM_RARITY_SECTION);
        damage = TextHelper.parse(Localization.ITEM_DAMAGE_SECTION);
        level = TextHelper.parse(Localization.ITEM_LEVEL_SECTION);
        sockets = TextHelper.parse(Localization.ITEM_SOCKETS_SECTION);
        socketcolon = TextHelper.parse(Localization.ITEM_SOCKETS_SECTION_COLONS);
        attributes = TextHelper.parse(Localization.ITEM_ATTRIBUTES_SECTIO);
        loreOrder = PluginConfig.ITEM_LORE_ORDER;
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
                if (a.isEmpty())
                    return;
                createDelimiter(attributes);
                Text.Builder builder = Text.builder();
                attributeMapToItemLorePart(a,builder);
            });
        }


        public void attributeMapToItemLorePart(Map<String, Integer> a, Text.Builder builder) {
            int k = 0;
            for (Map.Entry<String, Integer> e : a.entrySet()) {
                ICharacterAttribute attribute = NtRpgPlugin.GlobalScope.propertyService.getAttribute(e.getKey());
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

                builder.append(Text.builder()
                        .append(q)
                        .append(Text.builder(e.getValue() + " ").color(TextColors.WHITE).build())
                        .build());
                k++;
                if (k % 3 == 0) {
                    builder.append(Text.EMPTY);
                }
            }
        }

        public void createItemSocketsSection() {
            is.get(NKeys.ITEM_SOCKET_CONTAINER).ifPresent(a -> {
                if (a.isEmpty())
                    return;
                createDelimiter(sockets);
                for (ItemSocket itemSocket : a) {
                    SocketType type = itemSocket.getType();
                    t.add(Text.builder()
                            .append(TextHelper.parse(Localization.SOCKET_TYPES.get(type)))
                            .append(socketcolon)
                            .append(itemSocket.getContent() == null ? TextHelper.parse(Localization.SOCKET_EMPTY) : TextHelper.parse(itemSocket.getContent().getName()))
                            .build()
                    );
                    Map<String, EffectParams> effects = itemSocket.getContent().getEffects();
                    if (effects != null) {
                        itemEffectsToTextList(effects);
                    }
                }
            });
        }

        public void createItemMetaSection() {
            is.get(NKeys.ITEM_TYPE).ifPresent(a -> {
                createDelimiter(a);
                Text.Builder builder = Text.builder();
                is.get(NKeys.ITEM_RARITY).ifPresent(r -> {
                    builder.append(Text.builder().append(rarity).append(r).append(Text.NEW_LINE).build());
                });
                is.get(NKeys.ITEM_DAMAGE).ifPresent(r -> {
                    if (r.max == 0) {
                        builder.append(Text.builder()
                                .append(damage)
                                .append(Text.builder(String.valueOf(r.min))
                                        .color(NtRpgPlugin.GlobalScope.damageService.getColorByDamage(r.min))
                                        .build())
                                .append(Text.NEW_LINE)
                                .build());
                    } else {
                        builder.append(Text.builder()
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
                is.get(NKeys.ITEM_LEVEL).ifPresent(r -> {
                    builder.append(Text.builder().append(level)
                            .append(Text.builder(String.valueOf(r))
                                    .color(TextColors.YELLOW)
                                    .build())
                            .append(Text.NEW_LINE)
                            .build());
                });
                is.get(NKeys.ITEM_ATTRIBUTE_REQUIREMENTS).ifPresent(r -> {
                    attributeMapToItemLorePart(r,builder);
                });
                t.add(builder.build());
            });
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

        private void createItemTypeFirstLine() {
            is.get(NKeys.ITEM_TYPE).ifPresent(a -> {
                t.add(Text.builder("[ ").append(a).append(Text.builder(" ]").build()).build());
            });
        }

        public List<Text> buildLore() {
            createItemTypeFirstLine();
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
                }
            }
            return t;
        }
    }

    public static ItemLoreBuilder create(ItemStack is, List<Text> t) {
        return new ItemLoreBuilder(is, t);
    }
}
