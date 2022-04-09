package cz.neumimto.rpg.spigot.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class ItemResolver {

    Map<String, NamespacedItemDatabase> itemDatabase = new HashMap<>();

    public static ItemResolver instance;
    
    public void addDatabase(String key, NamespacedItemDatabase db) {
        itemDatabase.put(key, db);
    }

    public void init() {
        instance = this;
        itemDatabase.put("minecraft", new NamespacedItemDatabase() {
            @Override
            public ItemStack findById(NamespacedKey id) {
                Material material = Material.matchMaterial(id.value());
                if (material == null) {
                    material = Material.BARRIER;
                }
                return new ItemStack(material);
            }

            @Override
            public ItemStack findById(NamespacedKey key, int itemModel) {
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
        });
    }

    public ItemStack findById(String id) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(id);
        return findById(namespacedKey);
    }

    public ItemStack findById(NamespacedKey key) {
        return itemDatabase.get(key.getKey()).findById(key);
    }

    public ItemStack findById(String id, int itemModel) {
        NamespacedKey namespacedKey = NamespacedKey.fromString(id);
        return findById(namespacedKey, itemModel);
    }

    public ItemStack findById(NamespacedKey key, int itemModel) {
        return itemDatabase.get(key.getKey()).findById(key, itemModel);
    }

    public Set<String> getAll() {
        Set<String> set = new HashSet<>();
        for (NamespacedItemDatabase db : itemDatabase.values()) {
            set.addAll(db.getAll());
        }
        return set;
    }
}
