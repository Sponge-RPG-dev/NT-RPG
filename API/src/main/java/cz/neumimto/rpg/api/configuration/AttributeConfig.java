package cz.neumimto.rpg.api.configuration;

import cz.neumimto.config.blackjack.and.hookers.annotations.CustomAdapter;
import cz.neumimto.rpg.api.configuration.adapters.PropertiesMapAdapter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.Map;

@ConfigSerializable
public class AttributeConfig {

    @Setting("Id")
    private String id;

    @Setting("Name")
    private String name;

    @Setting("MaxValue")
    private float maxValue;

    @Setting("Properties")
    @CustomAdapter(PropertiesMapAdapter.class)
    private Map<Integer, Float> propBonus;

    @Setting("ItemType")
    private String itemType;

    @Setting("Description")
    private String description;

    public AttributeConfig(String id, String name, float maxValue, Map<Integer, Float> propBonus, String itemType, String description) {
        this.id = id;
        this.name = name;
        this.maxValue = maxValue;
        this.propBonus = propBonus;
        this.itemType = itemType;
        this.description = description;
    }

    public AttributeConfig() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, Float> getPropBonus() {
        return propBonus;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public String getItemType() {
        return itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
