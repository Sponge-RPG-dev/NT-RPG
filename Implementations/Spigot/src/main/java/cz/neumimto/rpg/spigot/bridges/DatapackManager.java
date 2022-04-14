package cz.neumimto.rpg.spigot.bridges;

import cz.neumimto.rpg.spigot.gui.GlyphResolver;
import cz.neumimto.rpg.spigot.items.NamespacedItemDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class DatapackManager {

    Map<String, NamespacedItemDatabase> itemDatabase = new HashMap<>();

    public static DatapackManager instance;

    private GlyphResolver glyphResolver;
    private NamespacedItemDatabase minecraft;

    public void addDatabase(String key, NamespacedItemDatabase db) {
        itemDatabase.put(key, db);
    }

    public Component resolveGlyphs(Player player, String string) {
        return MiniMessage.miniMessage().deserialize(glyphResolver.resolve(player, string));
    }

    public static void setGlyphResolver(GlyphResolver glyphResolver) {
        instance.glyphResolver = glyphResolver;
    }

    public void init() {
        instance = this;
        setGlyphResolver((player, input) -> input);

        minecraft = new NamespacedItemDatabase() {
            @Override
            public ItemStack findById(String id) {
                Material material = Material.matchMaterial(id);
                if (material == null) {
                    material = Material.BARRIER;
                }
                return new ItemStack(material);
            }

            @Override
            public ItemStack findById(String key, int itemModel) {
                ItemStack byId = findById(key);
                if (itemModel > 0) {
                    byId.editMeta(itemMeta -> itemMeta.setCustomModelData(itemModel));
                }
                return byId;
            }

            @Override
            public Collection<String> getAll() {
                return Stream.of(Material.values())
                        .filter(a -> !a.isLegacy())
                        .map(Material::getKey)
                        .map(NamespacedKey::toString)
                        .collect(Collectors.toSet());
            }
        };

        itemDatabase.put("minecraft", minecraft);
    }

    private String[] parse(String id) {
        return id.split(":");
    }

    public ItemStack findById(String id) {
        String[] parse = parse(id);
        return findById(parse[0], parse[1]);
    }

    public ItemStack findById(String namespace, String key) {
        return itemDatabase.getOrDefault(namespace, minecraft).findById(key);
    }

    public ItemStack findById(String key, int itemModel) {
        String[] parse = parse(key);
        return itemDatabase.getOrDefault(parse[0], minecraft).findById(parse[1], itemModel);
    }

    public Set<String> getAll() {
        Set<String> set = new HashSet<>();
        for (NamespacedItemDatabase db : itemDatabase.values()) {
            set.addAll(db.getAll());
        }
        return set;
    }
}
