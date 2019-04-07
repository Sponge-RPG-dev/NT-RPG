package cz.neumimto.rpg.api.items;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public interface ItemService {

    Optional<WeaponClass> getWeaponClassByName(String clazz);

    Set<RpgItemType> getItemTypesByWeaponClass(WeaponClass clazz);

    default Set<RpgItemType> getItemTypesByWeaponClass(String clazz) {
        Optional<WeaponClass> weaponClassByName = getWeaponClassByName(clazz);
        if (weaponClassByName.isPresent()) {
            return getItemTypesByWeaponClass(weaponClassByName.get().getName());
        }
        return Collections.emptySet();
    }

    void registerWeaponClass(WeaponClass weaponClass);

    Optional<RpgItemType> getRpgItemType(String itemId, String model);

    void registerRpgItemType(String itemId, String model, RpgItemType rpgItemType);

    void registerProperty(WeaponClass weaponClass, String property);
}
