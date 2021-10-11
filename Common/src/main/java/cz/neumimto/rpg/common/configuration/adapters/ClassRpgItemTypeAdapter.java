package cz.neumimto.rpg.common.configuration.adapters;

import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.items.ClassItem;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 5.1.2019.
 */

public class ClassRpgItemTypeAdapter implements Converter<Set<ClassItem>, List<String>> {

    @Override
    public Set<ClassItem> convertToField(List<String> list) {
        if (list == null) {
            return new HashSet<>();
        }
        Map<RpgItemType, Double> fromWeaponClass = new HashMap<>();
        Map<RpgItemType, Double> fromConfig = new HashMap<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith("weaponclass:")) {
                String[] data = s.split(":");
                String clazz = data[1];
                Set<RpgItemType> itemTypes = Rpg.get().getItemService().getItemTypesByWeaponClass(clazz);
                for (RpgItemType itemType : itemTypes) {
                    if (!fromWeaponClass.containsKey(itemType)) {
                        fromWeaponClass.put(itemType, itemType.getDamage());
                    }
                }
            } else {
                ItemString origin = ItemString.parse(s);
                for (ItemString parsed : Rpg.get().getItemService().parsePotentialItemStringWildcard(origin)) {
                    Optional<RpgItemType> rpgItemType = Rpg.get().getItemService().getRpgItemType(parsed.itemId, parsed.variant);
                    if (rpgItemType.isPresent()) {
                        RpgItemType rpgItemType1 = rpgItemType.get();
                        fromConfig.put(rpgItemType1, parsed.damage);
                    } else {
                        Log.error("- Not managed item type " + RpgItemType.KEY_BUILDER.apply(parsed.itemId, parsed.variant));
                    }
                }
            }
        }

        fromWeaponClass.putAll(fromConfig);

        return fromWeaponClass.entrySet()
                .stream()
                .map(a -> Rpg.get().getItemService().createClassItemSpecification(a.getKey(), a.getValue()))
                .collect(Collectors.toSet());
    }

    @Override
    public List<String> convertFromField(Set<ClassItem> value) {
        List<String> toSerialize = new ArrayList<>();
        for (ClassItem classItem : value) {
            RpgItemType type = classItem.getType();
            double damage = classItem.getDamage();
            StringBuilder item = new StringBuilder(type.getKey());
            item.append(";").append(damage);
            if (type.getModelId() != null && !type.getModelId().isEmpty()) {
                item.append(";").append(type.getModelId());
            }
            toSerialize.add(item.toString());
        }
        return toSerialize;
    }
}

