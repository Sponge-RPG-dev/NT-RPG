package cz.neumimto.rpg.inventory;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.players.properties.PropertyService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.*;


/**
 * Created by NeumimTo on 29.4.2018.
 */
@Singleton
public class ItemService {

    @Inject
    private PropertyService propertyService;


    private Map<ItemType, Set<RPGItemType>> itemTypes = new HashMap<>();
    private Map<ItemType, RPGItemType> armor = new HashMap<>();

    public RPGItemType getByConfigString(String k) {
        String[] split = k.split(";");
        String s = split[0];
        ItemType type = Sponge.getRegistry().getType(ItemType.class, s)
                    .orElseThrow(() -> new RuntimeException("Unknown item type " + s));
        if (split.length == 1) {
            return getByItemTypeAndName(type, (String) null);
        }
        return getByItemTypeAndName(type, split[0]);
    }

    public RPGItemType getByItemTypeAndName(ItemType itemType, String itemName) {
        Set<RPGItemType> rpgItemTypes = itemTypes.get(itemType);
        if (rpgItemTypes == null) {
            throw new RuntimeException("Unknown item type" + itemType);
        }
        for (RPGItemType rpgItemType : rpgItemTypes) {
            if (itemName == null && rpgItemType.getDisplayName() == null) {
                return rpgItemType;
            }
            if (itemName != null && itemName.equals(rpgItemType.getDisplayName())) {
                return rpgItemType;
            }
        }
        return null;
    }

    public RPGItemType getByItemTypeAndName(ItemType itemType, Text itemName) {
        return getByItemTypeAndName(itemType, itemName == null ? null : itemName.toPlain());
    }

    public RPGItemType getFromItemStack(ItemStack itemStack) {
        return getByItemTypeAndName(itemStack.getType(), itemStack.get(Keys.DISPLAY_NAME).orElse(null));
    }

    public RPGItemType getFromItemStack(ItemStackSnapshot itemStackSnapshot) {
        return getByItemTypeAndName(itemStackSnapshot.getType(), itemStackSnapshot.get(Keys.DISPLAY_NAME).orElse(null));
    }

    public void registerItemType(ItemType itemType, String itemName, WeaponClass weaponClass) {
        Set<RPGItemType> rpgItemTypes = itemTypes.computeIfAbsent(itemType, k -> new TreeSet<>(new RPGItemTypeComparator()));
        RPGItemType type = new RPGItemType(itemType, itemName, weaponClass);
        weaponClass.getItems().add(type);
        rpgItemTypes.add(type);
    }

    public void registerProperty(WeaponClass weaponClass, String property) {
        Short aShort = PropertyService.getAndIncrement.get();
        weaponClass.getProperties().add(property);
        propertyService.registerProperty(property, aShort);
        if (property.endsWith("_mult")) {
            propertyService.registerDefaultValue(aShort, 1.0f);
        }
    }

    public void registerItemArmorType(ItemType type) {
        armor.put(type, new RPGItemType(type,null, null));
    }

    public RPGItemType getArmorByItemType(ItemType type) {
        return armor.get(type);
    }

    private static class RPGItemTypeComparator implements Comparator<RPGItemType> {

        @Override
        public int compare(RPGItemType o1, RPGItemType o2) {
            if (o1.getDisplayName() == null)
                return -1;
            if (o2.getDisplayName() == null)
                return -1;
            return 1;
        }
    }
}
