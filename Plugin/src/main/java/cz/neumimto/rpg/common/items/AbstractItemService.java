package cz.neumimto.rpg.common.items;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.properties.PropertyService;

import javax.inject.Inject;
import java.util.*;

import static cz.neumimto.rpg.api.logging.Log.*;

public abstract class AbstractItemService implements ItemService {

    @Inject
    protected PropertyService propertyService;

    protected Map<String, RpgItemType> items = new HashMap<>();

    protected Map<String, WeaponClass> weaponClassMap = new HashMap<>();

    @Override
    public Optional<WeaponClass> getWeaponClassByName(String clazz) {
        return Optional.ofNullable(weaponClassMap.get(clazz));
    }

    @Override
    public Set<RpgItemType> getItemTypesByWeaponClass(WeaponClass clazz) {
        Set<RpgItemType> itemTypes = new HashSet<>();
        itemTypes.addAll(clazz.getItems());
        return getItemTypesByWeaponClass(clazz, itemTypes);
    }

    private Set<RpgItemType> getItemTypesByWeaponClass(WeaponClass clazz, Set<RpgItemType> itemTypes) {
        for (WeaponClass subClass : clazz.getSubClass()) {
            itemTypes.addAll(subClass.getItems());

            getItemTypesByWeaponClass(clazz, itemTypes);
        }
        return itemTypes;
    }

    @Override
    public void registerWeaponClass(WeaponClass weaponClass) {
        registerWeaponClassRecursivly(weaponClass);
    }

    private void registerWeaponClassRecursivly(WeaponClass weaponClass) {
        for (WeaponClass subClass : weaponClass.getSubClass()) {
            weaponClassMap.put(subClass.getName(), subClass);

            registerWeaponClassRecursivly(subClass);
        }
    }

    @Override
    public Optional<RpgItemType> getRpgItemType(String itemId, String model) {
        return Optional.ofNullable(items.get(RpgItemType.KEY_BUILDER.apply(itemId, model)));
    }

    @Override
    public void registerRpgItemType(RpgItemType rpgItemType) {
        rpgItemType.getWeaponClass().getItems().add(rpgItemType);
        items.put(RpgItemType.KEY_BUILDER.apply(rpgItemType.getId(), rpgItemType.getModelId()), rpgItemType);
    }

    @Override
    public void registerProperty(WeaponClass weaponClass, String property) {
        int val = PropertyService.getAndIncrement.get();

        if (!propertyService.exists(property)) {
            propertyService.registerProperty(property, val);
            propertyService.addPropertyToRequiresDamageRecalc(propertyService.getIdByName(property));
        }

        if (property.endsWith("_mult")) {
            propertyService.registerDefaultValue(val, 1.0f);
            weaponClass.getPropertiesMults().add(val);
        } else {
            weaponClass.getProperties().add(val);
        }

    }

    @Override
    public ClassItem createClassItemSpecification(RpgItemType key, Double value, IEffectSourceProvider provider) {
        value = NtRpgPlugin.pluginConfig.ITEM_DAMAGE_PROCESSOR.get(value, key.getDamage());
        return new ClassItemImpl(key, value, 0);
    }


    @Override
    public void loadItemGroups(Config config) {
        List<? extends Config> itemGroups = config.getConfigList("ItemGroups");
        loadWeaponGroups(itemGroups, null);

        for (String shield : config.getStringList("Armor")) {
            Optional<RpgItemType> rpgItemType = createRpgItemType(ItemString.parse(shield), WeaponClass.ARMOR);
            rpgItemType.ifPresent(this::registerRpgItemType);
        }
        for (String shield : config.getStringList("Shields")) {
            Optional<RpgItemType> rpgItemType = createRpgItemType(ItemString.parse(shield), WeaponClass.SHIELD);
            rpgItemType.ifPresent(this::registerRpgItemType);
        }
    }

    private void loadWeaponGroups(List<? extends Config> itemGroups, WeaponClass parent) {
        for (Config itemGroup : itemGroups) {
            String weaponClass;
            try {
                weaponClass = itemGroup.getString("WeaponClass");
            } catch (ConfigException e) {
                error("Could not read \"WeaponClass\" node, skipping. This is a critical miss configuration, some items will not be recognized "
                        + "as weapons");
                continue;
            }
            info(" - Loading weaponClass" + weaponClass);
            WeaponClass weapons = new WeaponClass(weaponClass);
            weapons.setParent(parent);
            registerWeaponClass(weapons);
            loadItemGroupsItems(itemGroup, weaponClass, weapons);
            loadItemGroupsProperties(itemGroup, weapons);
        }
    }

    private void loadItemGroupsItems(Config itemGroup, String weaponClass, WeaponClass weapons) {
        try {
            info("  - Reading \"Items\" config section" + weaponClass);
            List<String> items = itemGroup.getStringList("Items");
            for (String item : items) {
                ItemString parsed = ItemString.parse(item);
                Optional<RpgItemType> rpgItemType = createRpgItemType(parsed, weapons);
                rpgItemType.ifPresent(this::registerRpgItemType);
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

    private void loadItemGroupsProperties(Config itemGroup, WeaponClass weapons) {
        try {
            List<String> properties = itemGroup.getStringList("Properties");
            for (String property : properties) {
                registerProperty(weapons, property.toLowerCase());
            }
        } catch (ConfigException e) {
            warn("Properties configuration section not found, skipping");
        }
    }

    protected abstract Optional<RpgItemType> createRpgItemType(ItemString parsed, WeaponClass weapons);



}


