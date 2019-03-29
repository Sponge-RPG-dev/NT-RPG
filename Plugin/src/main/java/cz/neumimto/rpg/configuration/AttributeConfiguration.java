package cz.neumimto.rpg.configuration;

import cz.neumimto.config.blackjack.and.hookers.annotations.CustomAdapter;
import cz.neumimto.rpg.configuration.adapters.PropertiesMapAdapter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.ItemType;

import java.util.Map;
@ConfigSerializable
public class AttributeConfiguration {



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
    private ItemType itemType;

    @Setting("Description")
    private String description;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public Map<Integer, Float> getPropBonus() {
        return propBonus;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getDescription() {
        return description;
    }
}