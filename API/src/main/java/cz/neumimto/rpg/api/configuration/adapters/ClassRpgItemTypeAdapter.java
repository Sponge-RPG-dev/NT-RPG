package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.ItemString;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.logging.Log;

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
        Map<RpgItemType, Double> map = new HashMap<>();
        Map<RpgItemType, Double> map2 = new HashMap<>();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            if (s.toLowerCase().startsWith("weaponclass:")) {
                String[] data = s.split(":");
                String clazz = data[1];
                Set<RpgItemType> itemTypes = Rpg.get().getItemService().getItemTypesByWeaponClass(clazz);
                for (RpgItemType itemType : itemTypes) {
                    if (!map.containsKey(itemType)) {
                        map.put(itemType, itemType.getDamage());
                    }
                }
            } else {
                ItemString parsed = ItemString.parse(s);
                Optional<RpgItemType> rpgItemType = Rpg.get().getItemService().getRpgItemType(parsed.itemId, parsed.variant);
                if (rpgItemType.isPresent()) {
                    RpgItemType rpgItemType1 = rpgItemType.get();
                    map2.put(rpgItemType1, parsed.damage);
                } else {
                    Log.error("- Not managed item type " + RpgItemType.KEY_BUILDER.apply(parsed.itemId, parsed.variant));
                }
            }
        }

        map.putAll(map2);

        return map.entrySet()
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
            toSerialize.add(type.getKey().concat(";").concat(String.valueOf(damage)));
        }
        return toSerialize;
    }
}

