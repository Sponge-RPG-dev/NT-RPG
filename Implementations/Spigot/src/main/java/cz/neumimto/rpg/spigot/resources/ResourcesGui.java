package cz.neumimto.rpg.spigot.resources;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.ArrayList;
import java.util.List;

public class ResourcesGui {

    @Path("Resources")
    public List<ResourceGui> resources = new ArrayList<>();
}
