/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg;

import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import cz.neumimto.configuration.ConfigMapper;
import cz.neumimto.core.FindPersistenceContextEvent;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.configuration.Settings;
import cz.neumimto.rpg.listeners.DebugListener;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.persistance.model.CharacterSkill;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.utils.FileUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by NeumimTo on 29.4.2015.
 */
@Plugin(id = "nt-rpg", version = "1.0.0", name = "NT-Rpg", dependencies = {
        @Dependency(id = "MinecraftGuiServer", optional = true),
        @Dependency(id = "nt-core", version = "1.7",optional = false)
})
public class NtRpgPlugin {
    public static String workingDir;
    public static File pluginjar;
    public static GlobalScope GlobalScope;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path config;


    @Inject
    public Logger logger;

    @Listener
    public void registerEntities(FindPersistenceContextEvent event) {
        event.getClasses().add(CharacterBase.class);
        event.getClasses().add(BaseCharacterAttribute.class);
        event.getClasses().add(CharacterSkill.class);
        event.getClasses().add(CharacterClass.class);
    }

    @Listener
    public void onPluginLoad(GamePostInitializationEvent event) {
        long start = System.nanoTime();
        IoC ioc = IoC.get();
        Game game = Sponge.getGame();
        Optional<PluginContainer> gui = game.getPluginManager().getPlugin("MinecraftGUIServer");
        if (gui.isPresent()) {
            //ioc.registerInterfaceImplementation(MinecraftGuiService.class, game.getServiceManager().provide(MinecraftGuiService.class).get());
        } else {
            Settings.ENABLED_GUI = false;
        }
        ioc.registerDependency(this);

        try {
            workingDir = config.toString();
            URL url = FileUtils.getPluginUrl();
            pluginjar = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Path path = Paths.get(workingDir);
        ConfigMapper.init("NtRpg", path);
        ioc.registerDependency(ConfigMapper.get("NtRpg"));
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ioc.get(IoC.class, ioc);
        ResourceLoader rl = ioc.build(ResourceLoader.class);
        rl.loadJarFile(pluginjar, true);
        GlobalScope = ioc.build(GlobalScope.class);
        rl.loadExternalJars();
        ioc.postProcess();
        if (PluginConfig.DEBUG) {
            Sponge.getEventManager().registerListeners(this, ioc.build(DebugListener.class));
        }
        double elapsedTime = (System.nanoTime() - start) / 1000000000.0;
        logger.info("NtRpg plugin successfully loaded in " + elapsedTime + " seconds");
    }


}
