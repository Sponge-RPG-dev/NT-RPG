package cz.neumimto.rpg.spigot.resources;

import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

import java.util.List;

public class ResourceGui {
    @Path("Enabled")
    public boolean enabled;
    @Path("Type")
    public String type;
    @Path("Pattern")
    public String pattern;
    @Path("Display")
    public List<Display> display;
    @Path("RefreshRate")
    @Optional
    public int refreshRate;

    public static class Display {
        @Path("Resource")
        public String resource;

        @Path("Empty")
        public String empty;

        @Path("BarArray")
        public List<String> array;
    }
}
