package cz.neumimto.rpg.spigot.resources;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class SpigotResourceService extends ResourceService {

    @Inject
    private Injector injector;
    private Map<String, ResourceGui> guiRegistry = new HashMap<>();

    private Set<Integer> tasks = new HashSet<>();

    public SpigotResourceService() {
        super();
        injector.getInstance(UIActionbarIcons.class);
    }

    @Override
    public void reload() {
        for (Integer id : tasks) {
            Bukkit.getScheduler().cancelTask(id);
        }
        tasks.clear();

        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "Resources.conf");

        ResourcesGui gui = null;
        if (Files.exists(path)) {
            try (FileConfig fc = FileConfig.of(path)) {
                fc.load();
                gui = new ObjectConverter().toObject(fc, ResourcesGui::new);
            }
        }

        if (gui == null || gui.resources == null) {
            return;
        }

        for (ResourceGui resource : gui.resources) {
            if (resource.enabled) {
                switch (resource.type) {
                    case "actionbar_icons":
                        var uiActionbarIcons = new UIActionbarIcons(resource);
                        injector.injectMembers(uiActionbarIcons);
                        BukkitTask bukkitTask = Bukkit.getScheduler().runTask(SpigotRpgPlugin.getInstance(), uiActionbarIcons);
                        tasks.add(bukkitTask.getTaskId());
                        break;
                    case "actionbar_papi_text":
                        var papi = new UIActionbarPapiText(resource);
                        injector.injectMembers(papi);
                        BukkitTask bukkitTask2 = Bukkit.getScheduler().runTask(SpigotRpgPlugin.getInstance(), papi);
                        tasks.add(bukkitTask2.getTaskId());
                        break;
                }
            }
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
