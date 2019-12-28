package cz.neumimto.rpg.api.items;

import com.typesafe.config.Config;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.items.sockets.SocketType;
import cz.neumimto.rpg.api.items.subtypes.ItemSubtype;

import java.util.*;

public interface ItemService {

    void load();

    Map<String, ItemMetaType> getItemMetaTypes();

    Map<String, ItemSubtype> getItemSubtypes();

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

    ClassItem createClassItemSpecification(RpgItemType key, Double value);

    void loadItemGroups(Config c);

    boolean checkItemType(IActiveCharacter character, RpgItemStack rpgItemStack);

    boolean checkItemAttributeRequirements(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack);

    boolean checkItemClassRequirements(IActiveCharacter character, RpgItemStack rpgItemStack);

    void registerItemAttributes(Collection<AttributeConfig> attributes);

    Map<String, SocketType> getSocketTypes();

}
