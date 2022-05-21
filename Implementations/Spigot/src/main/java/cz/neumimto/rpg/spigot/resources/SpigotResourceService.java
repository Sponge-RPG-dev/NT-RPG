package cz.neumimto.rpg.spigot.resources;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Singleton
public class SpigotResourceService extends ResourceService {

    @Inject
    private AssetService assetService;

    @Inject
    private SpigotCharacterService characterService;

    private Supplier<Consumer<ISpigotCharacter>> uihandlerfactory;

    private Set<Integer> tasks = new HashSet<>();

    private Runnable refreshTask = () -> {
        for (ISpigotCharacter character : characterService.getCharacters()) {
            character.updateResourceUIHandler();
        }
    };

    private boolean init;


    @Override
    public void reload() {
        init = false;
        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "Resources.conf");
        if (!Files.exists(path)) {
            assetService.copyToFile("Resources.conf", path);
        }
    }

    //Initialize lazily, which makes it sure its initialized after ia/oraxen & papi if installed
    public void init () {
        init = true;
        for (Integer id : tasks) {
            Bukkit.getScheduler().cancelTask(id);
        }
        tasks.clear();

        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "Resources.conf");

        ResourcesGui gui = null;


        try (FileConfig fc = FileConfig.of(path)) {
            fc.load();
            gui = new ObjectConverter().toObject(fc, ResourcesGui::new);
        }


        if (gui == null || gui.resources == null) {
            return;
        }


        Optional<ResourceGui> first = gui.resources.stream().filter(a -> a.enabled).findFirst();
        if (first.isPresent()) {
            ResourceGui resourceGui = first.get();
            if (resourceGui.refreshRate > 0) {
                BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(SpigotRpgPlugin.getInstance(),
                        refreshTask,
                        0L, resourceGui.refreshRate);
                tasks.add(bukkitTask.getTaskId());
            }

            switch (resourceGui.type) {
                case "actionbar_icons" -> {
                    UIActionbarIcons.init(resourceGui);
                    uihandlerfactory = UIActionbarIcons::new;
                }
                case "actionbar_papi_text" -> {
                    UIActionbarPapiText.init(resourceGui);
                    uihandlerfactory = UIActionbarPapiText::new;
                }
            }
        }
    }

    @Override
    public void initializeForPlayer(IActiveCharacter activeCharacter) {
        super.initializeForPlayer(activeCharacter);
        if (!init){
            init();
        }
        if (uihandlerfactory != null) {
            Consumer<ISpigotCharacter> resHandler = uihandlerfactory.get();
            ((ISpigotCharacter)activeCharacter).setResourceUIHandler(resHandler);
        }
    }

    @Override
    protected Resource getHpTracker(IActiveCharacter character) {
        return new Health(character.getUUID());
    }

    @Override
    protected Resource getStaminaTracker(IActiveCharacter character) {
        return null;
    }

    @Override
    public Resource initializeForAi(AbstractMob mob) {
        return new MobHealth(mob.getUUID());
    }


}