package cz.neumimto.rpg.common.resources;

import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

import java.util.List;

public class ResourceConfig {
    @Path("Type")
    public String type;
    @Path("Display")
    public Display displayType;
    @Path("BarArray")
    @Optional
    public List<String> barCharArray;
}
