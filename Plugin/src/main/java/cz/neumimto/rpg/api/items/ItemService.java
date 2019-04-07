package cz.neumimto.rpg.api.items;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.effects.IEffectSourceProvider;

import java.nio.file.Path;
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

    void registerRpgItemType(RpgItemType rpgItemType);

    void registerProperty(WeaponClass weaponClass, String property);

    ClassItem createClassItemSpecification(RpgItemType key, Double value, IEffectSourceProvider provider);

    default void loadItemGroups(Path path) {
        Config config = ConfigFactory.parseFile(path.toFile());
        loadItemGroups(config);
    }

    public void loadItemGroups(Config c);
}
