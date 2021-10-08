package cz.neumimto.rpg.common;

import com.google.inject.Injector;
import cz.neumimto.rpg.common.effects.IGlobalEffect;
import cz.neumimto.rpg.common.effects.model.EffectModelMapper;
import cz.neumimto.rpg.common.skills.ISkill;

import java.io.IOException;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class AddonScanner {

    private static boolean stage;

    private static Path addonDir;

    private static ModuleLayer moduleLayer;

    public static ModuleLayer getModuleLayer() {
        return moduleLayer;
    }

    public static void setAddonDir(Path addonDir) {
        AddonScanner.addonDir = addonDir;
        if (!Files.exists(addonDir)) {
            try {
                Files.createDirectory(addonDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void onlyReloads() {
        AddonScanner.stage = true;
    }

    public static void prepareAddons() {
        if (!stage) {
            loadExternalResources();
        }
    }

    private static void loadExternalResources() {
        var pluginsFinder = ModuleFinder.of(addonDir);
        List<String> plugins = pluginsFinder
                .findAll()
                .stream()
                .map(ModuleReference::descriptor)
                .map(ModuleDescriptor::name)
                .collect(Collectors.toList());

        Configuration pluginsConfiguration = ModuleLayer
                .boot()
                .configuration()
                .resolve(pluginsFinder, ModuleFinder.of(), plugins);

        moduleLayer = ModuleLayer
                .boot()
                .defineModulesWithOneLoader(pluginsConfiguration, AddonScanner.class.getClassLoader());
    }

    public static List<ISkill> externalSkills(Injector injector) {
        return externalServices(ISkill.class, injector);
    }

    public static List<EventListener> externalEventListeners(Injector injector) {
        return externalServices(EventListener.class, injector);
    }

    public static List<IGlobalEffect> externalGlobalEffects(Injector injector) {
        return externalServices(IGlobalEffect.class, injector);
    }

    public static List<EffectModelMapper> externalEffectModelMappers(Injector injector) {
        return externalServices(EffectModelMapper.class, injector);
    }

    //todo
    public static List<RpgAddon> addons() { //todo
        return ServiceLoader.load(RpgAddon.class).stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
    }

    public static <T> List<T> externalServices(Class<T> t, Injector injector) {
        return ServiceLoader.load(moduleLayer, t).stream()
                .map(ServiceLoader.Provider::get)
                .peek(injector::injectMembers)
                .collect(Collectors.toList());
    }


    public static <T> Class<T> loadClass(String className, T as) {
        if (moduleLayer == null) {
            return null;
        }
        for (Module module : moduleLayer.modules()) {
            try {
                Class<T> t = (Class<T>) module.getClassLoader().loadClass(className);
                if (t != null) {
                    return t;
                }
            } catch (Throwable t) {
                t.printStackTrace(); //remove
            }
        }
        return null;
    }

}
