package cz.neumimto.rpg.common.items;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.configuration.ItemString;
import cz.neumimto.rpg.api.entity.IPropertyService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.items.*;
import cz.neumimto.rpg.api.items.sockets.SocketType;
import cz.neumimto.rpg.common.AbstractResourceManager;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.inventory.items.ItemMetaType;
import cz.neumimto.rpg.common.inventory.items.subtypes.ItemSubtype;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static cz.neumimto.rpg.api.logging.Log.*;

public abstract class AbstractItemService implements ItemService {

    @Inject
    protected IPropertyService propertyService;

    @Inject
    private AssetService assetService;

    protected Map<String, RpgItemType> items = new HashMap<>();

    protected Map<String, ItemClass> weaponClassMap = new HashMap<>();
    protected Map<String, SocketType> socketTypes = new HashMap<>();
    protected Map<String, ItemMetaType> itemMetaTypes = new HashMap<>();
    protected Map<String, ItemSubtype> itemSubtypes = new HashMap<>();

    protected Map<AttributeConfig, Integer> itemAttributesPlaceholder;


    @Override
    public Optional<ItemClass> getWeaponClassByName(String clazz) {
        return Optional.ofNullable(weaponClassMap.get(clazz));
    }

    @Override
    public Set<RpgItemType> getItemTypesByWeaponClass(ItemClass clazz) {
        Set<RpgItemType> itemTypes = new HashSet<>();
        itemTypes.addAll(clazz.getItems());
        return getItemTypesByWeaponClass(clazz, itemTypes);
    }

    private Set<RpgItemType> getItemTypesByWeaponClass(ItemClass clazz, Set<RpgItemType> itemTypes) {
        for (ItemClass subClass : clazz.getSubClass()) {
            itemTypes.addAll(subClass.getItems());

            getItemTypesByWeaponClass(clazz, itemTypes);
        }
        return itemTypes;
    }

    @Override
    public void registerWeaponClass(ItemClass itemClass) {
        weaponClassMap.put(itemClass.getName(), itemClass);

        for (ItemClass subClass : itemClass.getSubClass()) {
            weaponClassMap.put(subClass.getName(), subClass);
        }
    }


    @Override
    public Optional<RpgItemType> getRpgItemType(String itemId, String model) {
        return Optional.ofNullable(items.get(RpgItemType.KEY_BUILDER.apply(itemId, model)));
    }

    @Override
    public void registerRpgItemType(RpgItemType rpgItemType) {
        rpgItemType.getItemClass().getItems().add(rpgItemType);
        items.put(rpgItemType.getKey(), rpgItemType);
        info("   - Added managed Item " + rpgItemType.getItemClass().getName() + "/" + rpgItemType.getKey());
    }

    @Override
    public void registerProperty(ItemClass itemClass, String property) {
        int val = PropertyService.getAndIncrement.get();

        if (!propertyService.exists(property)) {
            propertyService.registerProperty(property, val);
            propertyService.addPropertyToRequiresDamageRecalc(propertyService.getIdByName(property));
        }

        if (property.endsWith("_mult")) {
            propertyService.registerDefaultValue(val, 1.0f);
            itemClass.getPropertiesMults().add(val);
        } else {
            itemClass.getProperties().add(val);
        }

    }

    @Override
    public ClassItem createClassItemSpecification(RpgItemType key, Double value) {
        value = Rpg.get().getPluginConfig().ITEM_DAMAGE_PROCESSOR.get(value, key.getDamage());
        return new ClassItemImpl(key, value, 0);
    }

    @Override
    public boolean checkItemType(IActiveCharacter character, RpgItemStack rpgItemStack) {
        RpgItemType itemType = rpgItemStack.getItemType();

        if (itemType.getItemClass() == ItemClass.ARMOR) {
            return character.getAllowedArmor().contains(itemType);
        } else {
            return character.getAllowedWeapons().containsKey(itemType);
        }
    }

    @Override
    public boolean checkItemAttributeRequirements(IActiveCharacter character, ManagedSlot managedSlot, RpgItemStack rpgItemStack) {
        Collection<AttributeConfig> attributes = propertyService.getAttributes().values();
        Map<AttributeConfig, Integer> inventoryRequirements = new HashMap<>();
        for (AttributeConfig attribute : attributes) {
            inventoryRequirements.put(attribute, 0);
        }
        character.getMinimalInventoryRequirements(inventoryRequirements);

        Map<AttributeConfig, Integer> bonusAttributes = rpgItemStack.getBonusAttributes();

        for (Map.Entry<AttributeConfig, Integer> entry : rpgItemStack.getMinimalAttributeRequirements().entrySet()) {
            AttributeConfig key = entry.getKey();
            Integer value = entry.getValue();
            Integer requirement = inventoryRequirements.get(key);

            Integer bonus = bonusAttributes.getOrDefault(entry.getKey(), 0);
            if (character.getAttributeValue(key) - bonus < Math.max(value, requirement)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void loadItemGroups(Path path) {
        File f = path.toFile();
        if (!f.exists()) {
            assetService.copyToFile("ItemGroups.conf", f.toPath());
        }

        Config c = ConfigFactory.parseFile(f);
        loadItemGroups(c);

    }

    @Override
    public void loadItemGroups(Config config) {
        info("Loading Weapon configuration");
        List<? extends Config> itemGroups = config.getConfigList("ItemGroups");
        loadWeaponGroups(itemGroups, null);

        info("Loading Armor configuration");
        for (String shield : config.getStringList("Armor")) {
            Optional<RpgItemType> rpgItemType = createRpgItemType(ItemString.parse(shield), ItemClass.ARMOR);
            rpgItemType.ifPresent(this::registerRpgItemType);
        }
        info("Loading Shields configuration");
        for (String shield : config.getStringList("Shields")) {
            Optional<RpgItemType> rpgItemType = createRpgItemType(ItemString.parse(shield), ItemClass.SHIELD);
            rpgItemType.ifPresent(this::registerRpgItemType);
        }
    }

    private void loadWeaponGroups(List<? extends Config> itemGroups, ItemClass parent) {
        for (Config itemGroup : itemGroups) {
            String weaponClass;
            try {
                weaponClass = itemGroup.getString("WeaponClass");
                info(" - Loading WeaponClass " + weaponClass);
                ItemClass weapons = new ItemClass(weaponClass);
                weapons.setParent(parent);
                if (parent != null) {
                    parent.getSubClass().add(weapons);
                }
                registerWeaponClass(weapons);
                loadItemGroupsItems(itemGroup, weaponClass, weapons);
                loadItemGroupsProperties(itemGroup, weapons);
            } catch (ConfigException e) {
                error("Could not read \"WeaponClass\" node, skipping. This is a critical miss configuration, some items will not be recognized as weapons");
            }
        }
    }

    private void loadItemGroupsItems(Config itemGroup, String weaponClass, ItemClass weapons) {
        try {
            info("  - Reading \"Items\" config section" + weaponClass);
            List<String> items = itemGroup.getStringList("Items");
            for (String item : items) {
                ItemString parsed = ItemString.parse(item);
                Optional<RpgItemType> rpgItemType = createRpgItemType(parsed, weapons);
                rpgItemType.ifPresent(a -> {
                    registerRpgItemType(a);
                    weapons.getItems().add(a);
                });
            }
        } catch (ConfigException e) {
            try {
                loadWeaponGroups(itemGroup.getConfigList("Items"), weapons);
            } catch (ConfigException ee) {
                warn("Could not read nested configuration for weapon class " + weaponClass + "This is a critical miss configuration, some items "
                        + "will not be recognized as weapons");
            }
        }
    }

    private void loadItemGroupsProperties(Config itemGroup, ItemClass weapons) {
        try {
            List<String> properties = itemGroup.getStringList("Properties");
            for (String property : properties) {
                registerProperty(weapons, property.toLowerCase());
            }
        } catch (ConfigException e) {
            warn("Properties configuration section not found, skipping");
        }
    }

    protected Map<AttributeConfig, Integer> parseItemAttributeMap(Map<String, Integer> stringIntegerMap) {
        Map<AttributeConfig, Integer> map = new HashMap<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : stringIntegerMap.entrySet()) {
            Optional<AttributeConfig> attr = propertyService.getAttributeById(stringIntegerEntry.getKey());
            if (attr.isPresent()) {
                map.put(attr.get(), stringIntegerEntry.getValue());
            }
        }
        return map;
    }

    protected abstract Optional<RpgItemType> createRpgItemType(ItemString parsed, ItemClass weapons);

    @Override
    public boolean checkItemClassRequirements(IActiveCharacter character, RpgItemStack rpgItemStack) {
        for (Map.Entry<ClassDefinition, Integer> entry : rpgItemStack.getClassRequirements().entrySet()) {

            PlayerClassData playerClassData = character.getClassByName(entry.getKey().getName());
            if (playerClassData == null) {
                return false;
            }
            if (playerClassData.getLevel() < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void registerItemAttributes(Collection<AttributeConfig> attributes) {
        this.itemAttributesPlaceholder = new HashMap<>();
        for (AttributeConfig attribute : attributes) {
            itemAttributesPlaceholder.put(attribute, 0);
        }
    }

    @Override
    public Map<String, SocketType> getSocketTypes() {
        return socketTypes;
    }

    public Map<String, ItemMetaType> getItemMetaTypes() {
        return itemMetaTypes;
    }

    public Map<String, ItemSubtype> getItemSubtypes() {
        return itemSubtypes;
    }
}


