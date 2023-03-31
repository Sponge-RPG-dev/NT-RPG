package cz.neumimto.rpg.spigot.inventory;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.effects.EffectParams;
import cz.neumimto.rpg.common.effects.IGlobalEffect;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.items.*;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.spigot.bridges.DatapackManager;
import cz.neumimto.rpg.spigot.items.SpigotRpgItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class SpigotItemService extends ItemService {

    public static final String EFFECTS = "ntrpg.effects";
    public static final String CLASS_REQUIREMENTS = "ntrpg.class-req";
    public static final String ATTRIBTUES = "ntrpg.attributes";
    public static final String ATTRIBTUES_REQUIREMENTS = "ntrpg.attribute-req";

    @Inject
    private PropertyService propertyService;

    private SpigotItemHandler itemHandler;

    public SpigotItemService() {
        itemHandler = new SpigotItemHandler();
    }

    public Optional<RpgItemType> getRpgItemType(ItemStack itemStack) {
        if (itemStack == null) {
            return Optional.empty();
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (meta.hasCustomModelData()) {
                int customModelData = meta.getCustomModelData();
                return getRpgItemType(itemStack.getType().getKey().toString(), String.valueOf(customModelData));
            }
        }
        return getRpgItemType(itemStack.getType().getKey().toString());

    }

    public Optional<RpgItemStack> getRpgItemStack(ItemStack itemStack) {
        return getRpgItemType(itemStack).map(a -> itemHandler.getItemStack(a, itemStack));
    }

    @Override
    protected Optional<RpgItemType> createRpgItemType(ItemString parsed, ItemClass wClass) {
        Material type = Material.matchMaterial(parsed.itemId);
        if (type == null) {
            Log.debug(" - Not Managed ItemType " + parsed.itemId);
            return Optional.empty();
        }

        return Optional.of(new SpigotRpgItemType(type.getKey().toString(), parsed.variant, wClass, type, parsed.permission));
    }

    @Override
    public Set<String> getAllItemIds() {
        return DatapackManager.instance.getAll();
    }

    public static class SpigotItemHandler {
        public RpgItemStack getItemStack(RpgItemType a, ItemStack is) {
            return new RpgItemStackImpl(a);
        }
    }

}
