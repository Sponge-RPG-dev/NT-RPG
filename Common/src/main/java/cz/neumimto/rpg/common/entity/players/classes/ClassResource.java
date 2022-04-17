package cz.neumimto.rpg.common.entity.players.classes;

import com.electronwill.nightconfig.core.conversion.Path;

public class ClassResource {
    @Path("Name")
    public String name;

    @Path("Value")
    public double baseValue;

    @Path("TickChange")
    public double tickChange;
}
