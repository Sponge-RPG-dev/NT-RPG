package cz.neumimto.rpg.players.attributes;

import cz.neumimto.config.blackjack.and.hookers.annotations.CustomAdapter;
import cz.neumimto.rpg.configuration.AttributeConfiguration;
import cz.neumimto.rpg.configuration.adapters.PropertiesMapAdapter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.item.ItemType;

import java.util.Map;

@ConfigSerializable
public class Attribute implements CatalogType {

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

    public Attribute(AttributeConfiguration a) {
        this(a.getId(), a.getName(), a.getMaxValue(), a.getPropBonus(), a.getItemType(), a.getDescription());
    }

    public Attribute(String id, String name, float maxValue, Map<Integer, Float> propBonus, ItemType itemType, String description) {
        this.id = id;
        this.name = name;
        this.maxValue = maxValue;
        this.propBonus = propBonus;
        this.itemType = itemType;
        this.description = description;
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

    public ItemType getItemType() {
        return itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
