package cz.neumimto.rpg.common.entity.players.classes;

import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

public class ClassResource {
    @Path("Type")
    public String type;

    @Path("Value")
    @Optional
    public double baseValue;

    @Path("TickChange")
    @Optional
    public double tickChange;
}
