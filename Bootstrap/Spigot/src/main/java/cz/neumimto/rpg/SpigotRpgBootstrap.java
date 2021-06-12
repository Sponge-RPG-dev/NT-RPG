package cz.neumimto.rpg;

import org.bukkit.plugin.java.JavaPlugin;

public class SpigotRpgBootstrap extends JavaPlugin {

    private NtRpgBootstrap bootstrap;
    private final String jarName = "ntrpg-embed.jar";

    @Override
    public void onEnable() {
        try {
            bootstrap.enable();
            bootstrap = RpgModuleLayer.getBootstrap(jarName, getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) {
            bootstrap.disable();
        }
    }
}
