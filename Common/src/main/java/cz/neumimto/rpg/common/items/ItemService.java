package cz.neumimto.rpg.common.items;

import com.typesafe.config.Config;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;

import java.util.*;

public interface ItemService {

    String DAMAGE_KEY = "dam";

    void load();

    List<ItemString> parsePotentialItemStringWildcard(ItemString i);

    void reload();

    Optional<ItemClass> getWeaponClassByName(String clazz);

    Set<RpgItemType> getItemTypesByWeaponClass(ItemClass clazz);

    default Set<RpgItemType> getItemTypesByWeaponClass(String clazz) {
        Optional<ItemClass> weaponClassByName = getWeaponClassByName(clazz);
        if (weaponClassByName.isPresent()) {
            return getItemTypesByWeaponClass(weaponClassByName.get());
        }
        return Collections.emptySet();
    }

    void registerWeaponClass(ItemClass itemClass);

    Optional<RpgItemType> getRpgItemType(String itemId, String model);

    void registerRpgItemType(RpgItemType rpgItemType);

    void registerProperty(ItemClass itemClass, String property);

    void loadItemGroups(Config c);

    boolean checkItemAttributeRequirements(IActiveCharacter character, RpgItemStack rpgItemStack);

    boolean checkItemClassRequirements(IActiveCharacter character, RpgItemStack rpgItemStack);

    void registerItemAttributes(Collection<AttributeConfig> attributes);

    boolean checkItemPermission(IActiveCharacter character, RpgItemStack rpgItemStack, String permSuffix);
}
