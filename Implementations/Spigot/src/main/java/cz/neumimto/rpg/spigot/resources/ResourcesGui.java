package cz.neumimto.rpg.spigot.resources;

import com.electronwill.nightconfig.core.conversion.Path;
import cz.neumimto.rpg.common.resources.ResourceDefinition;

import java.util.ArrayList;
import java.util.List;

public class ResourcesGui {

    @Path("Resources")
    public List<ResourceDefinition> resourcesConfig = new ArrayList<>();

    @Path("UI")
    public List<ResourceGui> resources = new ArrayList<>();
}
