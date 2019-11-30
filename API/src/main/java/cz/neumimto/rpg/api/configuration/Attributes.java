package cz.neumimto.rpg.api.configuration;

import com.electronwill.nightconfig.core.conversion.Path;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

/**
 * Created by NeumimTo on 10.2.2018.
 */
public class Attributes {

    @Path("Attributes")
    private List<AttributeConfig> attributes;

    public List<AttributeConfig> getAttributes() {
        return attributes;
    }
}
