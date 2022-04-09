package cz.neumimto.rpg;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.dependency.SoftDependency;
import org.bukkit.plugin.java.annotation.dependency.SoftDependsOn;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.io.File;

@Plugin(name = "NT-RPG", version = "2.1.0-SNAPSHOT-13")
@Description("Complete combat overhaul with classes and skills")
@Author("NeumimTo")
@Website("https://github.com/Sponge-RPG-dev/NT-RPG")
@LogPrefix("NTRPG")
@ApiVersion(ApiVersion.Target.v1_17)
@SoftDependsOn(
        value = {
                @SoftDependency("PlaceholderAPI"),
                @SoftDependency("HolographicDisplays"),
                @SoftDependency("MythicMobs"),
                @SoftDependency("MMOItems"),
                @SoftDependency("RPGRegions"),
                @SoftDependency("Mimic"),
                @SoftDependency("Oraxen")
        }
)
@DependsOn(
        value = {
                @Dependency("ProtocolLib")
        }
)
public class SpigotRpgBootstrap extends JavaPlugin {

    private NtRpgBootstrap bootstrap;
    private final static String jarName = "ntrpg-embed.jar";

    public SpigotRpgBootstrap() {
        super();
    }

    protected SpigotRpgBootstrap(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        try {
            bootstrap = RpgModuleLayer.getBootstrap(jarName, getClassLoader());
            bootstrap.enable(new NtRpgBootstrap.Data(this,
                    getDataFolder(),
                    new PaperCommandManager(this),
                    getLogger()));
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
