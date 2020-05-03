package cz.neumimto;

import cz.neumimto.rpg.api.properties.Property;
import cz.neumimto.rpg.api.properties.PropertyContainer;

/**
 * Created by NeumimTo on 5.7.2017.
 */
@PropertyContainer
public class AdditionalProperties {

    @Property(name = "stun_duration_mult", default_ = 1)
    public static int stun_duration_mult;
}
