package cz.neumimto.rpg.api.entity.players.attributes;

import cz.neumimto.rpg.sponge.configuration.AttributeConfiguration;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

/**
 * Created by NeumimTo on 10.2.2018.
 */
@ConfigSerializable
public class Attributes {

    @Setting("Attributes")
    private List<AttributeConfiguration> attributes;

    public List<AttributeConfiguration> getAttributes() {
        return attributes;
    }
}
