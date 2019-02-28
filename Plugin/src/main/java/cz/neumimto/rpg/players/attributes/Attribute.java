package cz.neumimto.rpg.players.attributes;

import cz.neumimto.config.blackjack.and.hookers.annotations.CustomAdapter;
import cz.neumimto.rpg.configuration.adapters.PropertiesMapAdapter;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.CatalogType;

import java.util.Map;

@ConfigSerializable
public class Attribute implements CatalogType {

    @Setting("Id")
    private String id;

    @Setting("Name")
    private String name;

    @Setting("Properties")
    @CustomAdapter(PropertiesMapAdapter.class)
    private Map<String, Float> propBonus;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Float> getPropBonus() {
        return propBonus;
    }
}
