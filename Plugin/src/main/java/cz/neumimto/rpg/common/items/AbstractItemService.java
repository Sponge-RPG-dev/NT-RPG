package cz.neumimto.rpg.common.items;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.inventory.ManagedSlot;
import cz.neumimto.rpg.api.items.*;
import cz.neumimto.rpg.common.configuration.ItemString;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.items.SpongeRpgItemType;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.properties.SpongePropertyService;

import javax.inject.Inject;
import java.util.*;

import static cz.neumimto.rpg.api.logging.Log.*;

public abstract class AbstractItemService implements ItemService {

    @Inject
    protected SpongePropertyService spongePropertyService;

    protected Map<String, RpgItemType> items = new HashMap<>();

    protected Map<String, ItemClass> weaponClassMap = new HashMap<>();
    protected Map<Attribute, Integer> itemAttributesPlaceholder;

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
        items.put(RpgItemType.KEY_BUILDER.apply(rpgItemType.getId(), rpgItemType.getModelId()), rpgItemType);
    }

    @Override
    public void registerProperty(ItemClass itemClass, String property) {
        int val = PropertyServiceImpl.getAndIncrement.get();

        if (!spongePropertyService.exists(property)) {
            spongePropertyService.registerProperty(property, val);
            spongePropertyService.addPropertyToRequiresDamageRecalc(spongePropertyService.getIdByName(property));
        }

        if (property.endsWith("_mult")) {
            spongePropertyService.registerDefaultValue(val, 1.0f);
            itemClass.getPropertiesMults().add(val);
        } else {
            itemClass.getProperties().add(val);
        }

    }

    @Override
    public ClassItem createClassItemSpecification(RpgItemType key, Double value, IEffectSourceProvider provider) {
        value = NtRpgPlugin.pluginConfig.ITEM_DAMAGE_PROCESSOR.get(value, key.getDamage());
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
        Collection<Attribute> attributes = Rpg.get().getAttributes();
        Map<Attribute, Integer> inventoryRequirements = new HashMap<>();
        for (Attribute attribute : attributes) {
            inventoryRequirements.put(attribute, 0);
        }
        character.getMinimalInventoryRequirements(inventoryRequirements);

        Map<Attribute, Integer> bonusAttributes = rpgItemStack.getBonusAttributes();

        for (Map.Entry<Attribute, Integer> entry : rpgItemStack.getMinimalAttributeRequirements().entrySet()) {
            Attribute key = entry.getKey();
            Integer value = entry.getValue();
            Integer requirement = inventoryRequirements.get(key);

            Integer bonus = bonusAttributes.get(entry.getKey());
            if (character.getAttributeValue(key) - bonus < Math.max(value, requirement)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void loadItemGroups(Config config) {
        List<? extends Config> itemGroups = config.getConfigList("ItemGroups");
        loadWeaponGroups(itemGroups, null);

        for (String shield : config.getStringList("Armor")) {
            Optional<SpongeRpgItemType> rpgItemType = createRpgItemType(ItemString.parse(shield), ItemClass.ARMOR);
            rpgItemType.ifPresent(this::registerRpgItemType);
        }
        for (String shield : config.getStringList("Shields")) {
            Optional<SpongeRpgItemType> rpgItemType = createRpgItemType(ItemString.parse(shield), ItemClass.SHIELD);
            rpgItemType.ifPresent(this::registerRpgItemType);
        }
    }

    private void loadWeaponGroups(List<? extends Config> itemGroups, ItemClass parent) {
        for (Config itemGroup : itemGroups) {
            String weaponClass;
            try {
                weaponClass = itemGroup.getString("WeaponClass");
                info(" - Loading WeaponClass" + weaponClass);
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
                Optional<SpongeRpgItemType> rpgItemType = createRpgItemType(parsed, weapons);
                rpgItemType.ifPresent(this::registerRpgItemType);
                rpgItemType.ifPresent(a -> weapons.getItems().add(a));
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

    protected Map<Attribute, Integer> parseItemAttributeMap(Map<String, Integer> stringIntegerMap) {
        Map<Attribute, Integer> map = new HashMap<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : stringIntegerMap.entrySet()) {
            Optional<Attribute> attr = spongePropertyService.getAttributeById(stringIntegerEntry.getKey());
            if (attr.isPresent()) {
                map.put(attr.get(), stringIntegerEntry.getValue());
            }
        }
        return map;
    }

    protected abstract Optional<SpongeRpgItemType> createRpgItemType(ItemString parsed, ItemClass weapons);

    @Override
    public boolean checkItemClassRequirements(IActiveCharacter character, RpgItemStack rpgItemStack) {
        for (Map.Entry<ClassDefinition, Integer> entry : rpgItemStack.getClassRequirements().entrySet()) {
            PlayerClassData playerClassData = character.getClasses().get(entry.getKey().getName());
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
    public void registerItemAttributes(Collection<Attribute> attributes) {
        this.itemAttributesPlaceholder = new HashMap<>();
        for (Attribute attribute : attributes) {
            itemAttributesPlaceholder.put(attribute, 0);
        }
    }
}


