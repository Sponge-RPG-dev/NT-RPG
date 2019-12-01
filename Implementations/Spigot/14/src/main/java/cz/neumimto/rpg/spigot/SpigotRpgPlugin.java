package cz.neumimto.rpg.spigot;

import co.aikar.commands.CommandManager;
import co.aikar.commands.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.AbstractResourceManager;
import cz.neumimto.rpg.common.AddonScanner;
import cz.neumimto.rpg.persistence.flatfiles.FlatFilesModule;
import cz.neumimto.rpg.spigot.commands.SpigotAdminCommands;
import cz.neumimto.rpg.spigot.commands.SpigotCharacterCommands;
import cz.neumimto.rpg.spigot.resources.SpigotGuiceModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;


@Plugin(name = "NT-RPG", version = "0.0.1-SNAPSHOT")
@Description("Complete combat overhaul with classes and skills")
@Author("NeumimTo")
@Website("https://github.com/Sponge-RPG-dev/NT-RPG")
@LogPrefix("NTRPG")
@ApiVersion(ApiVersion.Target.v1_13)
public class SpigotRpgPlugin extends JavaPlugin {

    private static SpigotRpgPlugin plugin;

    private static Logger logger = LoggerFactory.getLogger("NTRPG");

    public static SpigotRpgPlugin getInstance() {
        return plugin;
    }


    @Override
    public void onEnable() {
        Log.setLogger(logger);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        plugin = this;

        Path workingDirPath = getDataFolder().toPath();

        SpigotRpg spigotRpg = new SpigotRpg(workingDirPath.toString());


        CommandManager manager = new PaperCommandManager(this);

        spigotRpg.init(
                getDataFolder().toPath(),
                manager,
                new Class[]{SpigotAdminCommands.class, SpigotCharacterCommands.class},
                new FlatFilesModule(),
                (bindings, providers) -> new SpigotGuiceModule(this,  bindings, providers),
                injector -> {
                    injector.injectMembers(spigotRpg);
                    new RpgImpl(spigotRpg);
                }

        );

    }
}
