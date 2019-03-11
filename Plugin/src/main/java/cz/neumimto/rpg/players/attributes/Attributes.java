package cz.neumimto.rpg.players.attributes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

/**
 * Created by NeumimTo on 10.2.2018.
 */
@ConfigSerializable
public class Attributes {

    @Setting("Attributes")
    private List<Attribute> attributes;

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
