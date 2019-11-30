package cz.neumimto.rpg.api.items;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.items.sockets.SocketType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public interface ItemService {

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

    ClassItem createClassItemSpecification(RpgItemType key, Double value);

    void loadItemGroups(Path path);

    void loadItemGroups(Config c);

    boolean checkItemType(IActiveCharacter character, RpgItemStack rpgItemStack);

    boolean checkItemAttributeRequirements(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack);

    boolean checkItemClassRequirements(IActiveCharacter character, RpgItemStack rpgItemStack);

    void registerItemAttributes(Collection<AttributeConfig> attributes);

    Map<String, SocketType> getSocketTypes();
}
