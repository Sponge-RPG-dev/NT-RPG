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

package cz.neumimto.gui;

import com.google.common.base.Optional;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.IoC;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.utils.FileUtils;
import djxy.api.MinecraftGuiService;
import djxy.controllers.CSSFactory;
import djxy.controllers.ComponentFactory;
import djxy.controllers.ResourceFactory;
import djxy.models.ComponentManager;
import djxy.models.Form;
import djxy.models.component.Component;
import djxy.models.resource.Resource;
import org.spongepowered.api.Game;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by NeumimTo on 8.8.2015.
 */
@Singleton
public class NComponentManager implements ComponentManager {

    @Inject
    private Game game;

    @Inject
    private NtRpgPlugin plugin;

    private MinecraftGuiService minecraftGuiService;

    public Component root;

    private List<Resource> resource;

    @PostProcess(priority = 500)
    public void initializeComponents() {
        Optional<MinecraftGuiService> provide = game.getServiceManager().provide(MinecraftGuiService.class);
        if (provide.isPresent()) {
            minecraftGuiService = provide.get();
            IoC.get().registerInterfaceImplementation(MinecraftGuiService.class, minecraftGuiService);
            Path path = Paths.get(NtRpgPlugin.workingDir + File.separator + "GuiComponents.xml");
            FileUtils.createFileIfNotExists(path);
            Path css = Paths.get(NtRpgPlugin.workingDir + File.separator + "GuiAttributes.css");
            FileUtils.createFileIfNotExists(css);
            root = ComponentFactory.load(path.toFile(), CSSFactory.load(css.toFile()));
            Path resource = Paths.get(NtRpgPlugin.workingDir + File.separator + "GuiResources.xml");
            FileUtils.createFileIfNotExists(resource);
            this.resource = ResourceFactory.load(resource.toFile());
            minecraftGuiService.registerComponentManager(this, false);
        }
    }

    @Override
    public void initPlayerGUI(String playerUUID) {
        game.getScheduler().createTaskBuilder().async().execute(() -> {
            minecraftGuiService.createComponent(playerUUID, root);
            resource.stream().forEach(r -> {
                minecraftGuiService.downloadResource(playerUUID, r);
            });
        }).submit(plugin);
    }

    @Override
    public void receiveForm(String playerUUID, Form form) {

    }


}
