package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;

import java.util.Map;

public class SpigotGuiceModuleBuilder {
    private SpigotRpgPlugin ntRpgPlugin;
    private SpigotRpg spigotRpg;
    private Map extraBindings;
    private Map providers;
    private String minecraftVersion;

    public SpigotGuiceModuleBuilder setNtRpgPlugin(SpigotRpgPlugin ntRpgPlugin) {
        this.ntRpgPlugin = ntRpgPlugin;
        return this;
    }

    public SpigotGuiceModuleBuilder setSpigotRpg(SpigotRpg spigotRpg) {
        this.spigotRpg = spigotRpg;
        return this;
    }

    public SpigotGuiceModuleBuilder setExtraBindings(Map extraBindings) {
        this.extraBindings = extraBindings;
        return this;
    }

    public SpigotGuiceModuleBuilder setProviders(Map providers) {
        this.providers = providers;
        return this;
    }

    public SpigotGuiceModuleBuilder setMinecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
        return this;
    }

    public SpigotGuiceModule createSpigotGuiceModule() {
        return new SpigotGuiceModule(ntRpgPlugin, spigotRpg, extraBindings, providers, minecraftVersion);
    }
}