package cz.neumimto.rpg.common.items;

import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.api.items.WeaponClass;
import cz.neumimto.rpg.properties.PropertyService;

import javax.inject.Inject;
import java.util.*;

public class ItemServiceImpl implements ItemService {

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
    public void registerRpgItemType(String itemId, String model, RpgItemType rpgItemType) {
        items.put(RpgItemType.KEY_BUILDER.apply(itemId, model), rpgItemType);
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
    }}
