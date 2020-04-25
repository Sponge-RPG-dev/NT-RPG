package cz.neumimto.rpg.spigot.inventory;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.configuration.ItemString;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.items.RpgItemStack;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.items.AbstractItemService;
import cz.neumimto.rpg.common.items.RpgItemStackImpl;
import cz.neumimto.rpg.spigot.items.SpigotRpgItemType;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTListCompound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class SpigotItemService extends AbstractItemService {

    public static final String EFFECTS = "ntrpg.effects";
    public static final String CLASS_REQUIREMENTS = "ntrpg.class-req";
    public static final String ATTRIBTUES = "ntrpg.attributes";
    public static final String ATTRIBTUES_REQUIREMENTS = "ntrpg.attribute-req";

    @Inject
    private PropertyService propertyService;

    private SpigotItemHandler itemHandler;

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
        return getRpgItemType(itemStack).map(a -> {
            NBTItem nbtItem = new NBTItem(itemStack);
            return itemHandler.getItemStack(a, nbtItem);
        });
    }


    public RpgItemStack getRpgItemStack(RpgItemType type, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return itemHandler.getItemStack(type, nbtItem);
    }

    @Override
    protected Optional<RpgItemType> createRpgItemType(ItemString parsed, ItemClass wClass) {
        Material type = Material.matchMaterial(parsed.itemId);
        if (type == null) {
            Log.error(" - Not Managed ItemType " + parsed.itemId);
            return Optional.empty();
        }

        return Optional.of(new SpigotRpgItemType(type.getKey().toString(), parsed.variant, wClass, parsed.damage, parsed.armor, type));
    }

    public static class SpigotItemHandler {
        public RpgItemStack getItemStack(RpgItemType a, NBTItem nbtItem) {
            return new RpgItemStackImpl(a,
                    getItemEffects(nbtItem),
                    getItemBonusAttributes(nbtItem),
                    getItemMinimalAttributeRequirements(nbtItem),
                    getClassRequirements(nbtItem),
                    getItemData(nbtItem));
        }

        protected Map<String, Double> getItemData(NBTItem nbtItem) {
            NBTCompoundList attributes = nbtItem.getCompoundList("AttributeModifiers");
            if (attributes == null || attributes.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, Double> data = new HashMap<>();
            for (NBTListCompound attribute : attributes) {
                if (attribute.getString("AttributeName").equals("generic.attackDamage")) {
                    double amount = attribute.getDouble("Amount");
                    data.put(DAMAGE_KEY, amount);
                }
            }
            return data;
        }

        protected Map<IGlobalEffect, EffectParams> getItemEffects(NBTItem nbtItem) {
            NBTCompoundList compoundList = nbtItem.getCompoundList(EFFECTS);
            if (compoundList == null) {
                return Collections.emptyMap();
            }
            Map<IGlobalEffect, EffectParams> map = new HashMap<>();
            for (int i = 0; i < compoundList.size(); i++) {
                NBTListCompound nbtListCompound = compoundList.get(i);
                for (String key : nbtListCompound.getKeys()) {

                }
            }
            return map;
        }

        protected Map<ClassDefinition, Integer> getClassRequirements(NBTItem nbtItem) {
            return Collections.emptyMap();
        }

        protected Map<AttributeConfig, Integer> getItemBonusAttributes(NBTItem nbtItem) {
            return Collections.emptyMap();
        }

        protected Map<AttributeConfig, Integer> getItemMinimalAttributeRequirements(NBTItem nbtItem) {
            NBTCompoundList compoundList = nbtItem.getCompoundList(ATTRIBTUES_REQUIREMENTS);
            if (compoundList == null) {
                return Collections.emptyMap();
            }
            Map<AttributeConfig, Integer> map = new HashMap<>();
            for (NBTListCompound nbtListCompound : compoundList) {
                for (String key : nbtListCompound.getKeys()) {
                    int integer = nbtListCompound.getInteger(key);
                    Optional<AttributeConfig> attributeById = Rpg.get().getPropertyService().getAttributeById(key);
                    if (attributeById.isPresent()) {
                        AttributeConfig attr = attributeById.get();
                        map.put(attr, integer);
                    } else {
                        Log.warn("Discovered an unknown attribute on an intemstack " + key);
                    }
                }
            }
            return map;
        }
    }

    public SpigotItemHandler getItemHandler() {
        return itemHandler;
    }

    public void setItemHandler(SpigotItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }
}
