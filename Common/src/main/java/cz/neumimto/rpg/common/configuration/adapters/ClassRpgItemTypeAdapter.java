package cz.neumimto.rpg.common.configuration.adapters;

import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.logging.Log;

import java.util.*;

/**
 * Created by NeumimTo on 5.1.2019.
 */

public class ClassRpgItemTypeAdapter implements Converter<Set<RpgItemType>, List<String>> {

    @Override
    public Set<RpgItemType> convertToField(List<String> list) {
        if (list == null) {
            return new HashSet<>();
        }
        Set<RpgItemType> fromWeaponClass = new HashSet<>();
        Set<RpgItemType> fromConfig = new HashSet<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith("weaponclass:")) {
                String[] data = s.split(":");
                String clazz = data[1];
                Set<RpgItemType> itemTypes = Rpg.get().getItemService().getItemTypesByWeaponClass(clazz);
                for (RpgItemType itemType : itemTypes) {
                     fromWeaponClass.add(itemType);
                }
            } else {
                ItemString origin = ItemString.parse(s);
                for (ItemString parsed : Rpg.get().getItemService().parsePotentialItemStringWildcard(origin)) {
                    Optional<RpgItemType> rpgItemType = Rpg.get().getItemService().getRpgItemType(parsed.itemId, parsed.variant);
                    if (rpgItemType.isPresent()) {
                        RpgItemType rpgItemType1 = rpgItemType.get();
                        fromConfig.add(rpgItemType1);
                    } else {
                        Log.debug("- Not managed item type " + RpgItemType.KEY_BUILDER.apply(parsed.itemId, parsed.variant));
                    }
                }
            }
        }

        fromWeaponClass.addAll(fromConfig);

        return fromWeaponClass;
    }

    @Override
    public List<String> convertFromField(Set<RpgItemType> value) {
        List<String> toSerialize = new ArrayList<>();
        return toSerialize;
    }
}

