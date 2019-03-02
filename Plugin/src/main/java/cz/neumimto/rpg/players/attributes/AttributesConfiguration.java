package cz.neumimto.rpg.players.attributes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

/**
 * Created by NeumimTo on 2.3.2019.
 */
@ConfigSerializable
public class AttributesConfiguration {

    @Setting
    private List<Attribute> attributes;
}
