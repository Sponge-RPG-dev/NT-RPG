package cz.neumimto.rpg.common.items;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.utils.Wildcards;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.common.logging.Log.*;

public abstract class AbstractItemService implements ItemService {

    protected Map<String, RpgItemType> items = new HashMap<>();
    protected Map<String, ItemClass> weaponClassMap = new HashMap<>();
    protected Map<AttributeConfig, Integer> itemAttributesPlaceholder = new HashMap<>();

    @Inject
    protected PropertyService propertyService;
    @Inject
    private AssetService assetService;
    @Inject
    private PermissionService permissionService;

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

            getItemTypesByWeaponClass(subClass, itemTypes);
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
        String iKey = RpgItemType.KEY_BUILDER.apply(itemId, model);
        RpgItemType rpgItemType = items.get(iKey);
        if (rpgItemType == null && model != null) {
            rpgItemType = items.get(itemId);
        }
        return Optional.ofNullable(rpgItemType);
    }

    protected Optional<RpgItemType> getRpgItemType(String itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public void registerRpgItemType(RpgItemType rpgItemType) {
        rpgItemType.getItemClass().getItems().add(rpgItemType);
        items.put(rpgItemType.getKey(), rpgItemType);
        debug("   - Added managed Item " + rpgItemType.getItemClass().getName() + "/" + rpgItemType.getKey());
    }

    @Override
    public void registerProperty(ItemClass itemClass, String property) {
        int val = PropertyService.getAndIncrement.get();

        if (!propertyService.exists(property)) {
            propertyService.registerProperty(property, val);
        }

        if (property.endsWith("_mult")) {
            propertyService.registerDefaultValue(val, 1.0f);
            itemClass.getPropertiesMults().add(val);
        } else {
            itemClass.getProperties().add(val);
        }
    }

    @Override
    public boolean checkItemType(IActiveCharacter character, RpgItemStack rpgItemStack) {
        RpgItemType itemType = rpgItemStack.getItemType();

        if (itemType.getItemClass() == ItemClass.ARMOR) {
            return character.getAllowedArmor().contains(itemType);
        } else {
            return character.getAllowedWeapons().contains(itemType);
        }
    }

    @Override
    public boolean checkItemAttributeRequirements(IActiveCharacter character, RpgItemStack rpgItemStack) {
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
    public void load() {
        File f = Paths.get(Rpg.get().getWorkingDirectory()).resolve("ItemGroups.conf").toFile();
        if (!f.exists()) {
            assetService.copyToFile("ItemGroups.conf", f.toPath());
        }

        Config c = ConfigFactory.parseFile(f);
        loadItemGroups(c);
    }

    @Override
    public void loadItemGroups(Config config) {
        debug("Loading Weapon configuration");
        if (config.hasPath("ItemGroups")) {
            List<? extends Config> itemGroups = config.getConfigList("ItemGroups");
            loadWeaponGroups(itemGroups, null);
        } else {
            Log.error("Missing ItemGroups section");
        }

        debug("Loading Armor configuration");
        if (config.hasPath("Armor")) {
            for (String armor : config.getStringList("Armor")) {
                ItemString parse = ItemString.parse(armor);
                for (ItemString itemString : parsePotentialItemStringWildcard(parse)) {
                    Optional<RpgItemType> rpgItemType = createRpgItemType(itemString, ItemClass.ARMOR);
                    rpgItemType.ifPresent(this::registerRpgItemType);
                }
            }
        } else {
            Log.error("Missing Armor section");
        }

        debug("Loading Shields configuration");
        if (config.hasPath("Shields")) {
            for (String shield : config.getStringList("Shields")) {
                ItemString parse = ItemString.parse(shield);

                for (ItemString itemString : parsePotentialItemStringWildcard(parse)) {
                    Optional<RpgItemType> rpgItemType = createRpgItemType(itemString, ItemClass.SHIELD);
                    rpgItemType.ifPresent(this::registerRpgItemType);
                }
            }
        } else {
            Log.error("Missing Shields section");
        }

        debug("Loading Projectiles configuration");
        if (config.hasPath("Projectiles")) {
            List<? extends Config> projectiles = config.getConfigList("Projectiles");
            loadWeaponGroups(projectiles, ItemClass.PROJECTILES);
        } else {
            Log.error("Missing Projectiles section");
        }
    }

    private void loadWeaponGroups(List<? extends Config> itemGroups, ItemClass parent) {
        for (Config itemGroup : itemGroups) {
            String weaponClass;
            try {
                weaponClass = itemGroup.getString("Class");
                debug(" - Loading WeaponClass " + weaponClass);
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
            debug("  - Reading \"Items\" config section" + weaponClass);
            List<String> items = itemGroup.getStringList("Items");
            for (String item : items) {
                ItemString parse = ItemString.parse(item);
                for (ItemString itemString : parsePotentialItemStringWildcard(parse)) {
                    Optional<RpgItemType> rpgItemType = createRpgItemType(itemString, weapons);
                    rpgItemType.ifPresent(a -> {
                        registerRpgItemType(a);
                        weapons.getItems().add(a);
                    });
                }
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
            debug("Properties configuration section not found, skipping");
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
        for (AttributeConfig attribute : attributes) {
            itemAttributesPlaceholder.put(attribute, 0);
        }
    }


    public abstract Set<String> getAllItemIds();

    @Override
    public List<ItemString> parsePotentialItemStringWildcard(ItemString i) {
        String itemId = i.itemId;
        if (!itemId.contains("*")) {
            return Collections.singletonList(i);
        }
        return Wildcards.substract(itemId, getAllItemIds())
                .stream()
                .map(a -> new ItemString(a, i.variant, i.permission))
                .collect(Collectors.toList());
    }

    @Override
    public void reload() {
        items.clear();
        weaponClassMap.clear();
        itemAttributesPlaceholder.clear();

        load();
    }

    @Override
    public boolean checkItemPermission(IActiveCharacter character, RpgItemStack rpgItemStack) {
        String permission = rpgItemStack.getItemType().getPermission();
        if (permission == null) {
            return true;
        }
        return permissionService.hasPermission(character, permission);
    }
}


