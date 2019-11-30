package cz.neumimto.rpg.spigot;

import co.aikar.commands.CommandManager;
import co.aikar.commands.PaperCommandManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.AddonScanner;
import cz.neumimto.rpg.common.commands.ACFBootstrap;
import cz.neumimto.rpg.persistence.flatfiles.FlatFilesModule;
import cz.neumimto.rpg.spigot.commands.SpigotAdminCommands;
import cz.neumimto.rpg.spigot.commands.SpigotCharacterCommands;
import cz.neumimto.rpg.spigot.resources.SpigotGuiceModule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


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


        AddonScanner.setAddonDir(getDataFolder().toPath().resolve("addons"));
        AddonScanner.setDeployedDir(getDataFolder().toPath().resolve(".deployed"));

        AddonScanner.prepareAddons();
        AddonScanner.onlyReloads();

        Set<Class<?>> classesToLoad = AddonScanner.getClassesToLoad();
        Iterator<Class<?>> iterator = classesToLoad.iterator();

        Map extraBindings = new HashMap();
        FlatFilesModule flatFilesModule = new FlatFilesModule();
        extraBindings.putAll(flatFilesModule.getBindings());
        Map<Class<?>, ?> providers = new HashMap();
        Injector injector;
        try {
            while (iterator.hasNext()) {
                Class<?> next = iterator.next();
                if (RpgAddon.class.isAssignableFrom(next)) {
                    try {
                        RpgAddon addon = (RpgAddon) next.getConstructor().newInstance();
                        extraBindings = addon.getBindings();
                        Map map = new HashMap<>();
                        map.put("WORKINGDIR", getDataFolder().getAbsolutePath().toString());
                        providers = addon.getProviders(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

             injector = Guice.createInjector(new SpigotGuiceModule(this, extraBindings, providers));
        } catch (Exception e) {
            Log.error("Could not create Guice Injector", e);
            return;
        }
        SpigotRpg spigotRpg = new SpigotRpg(getDataFolder().getAbsolutePath());
        injector.injectMembers(spigotRpg);
        new RpgImpl(spigotRpg);
        Rpg.get().getResourceLoader().initializeComponents();
        spigotRpg.postInit();

        CommandManager manager = new PaperCommandManager(this);

        ACFBootstrap.initializeACF(manager,
                                    injector.getInstance(SpigotAdminCommands.class),
                                    injector.getInstance(SpigotCharacterCommands.class));


    }
}
