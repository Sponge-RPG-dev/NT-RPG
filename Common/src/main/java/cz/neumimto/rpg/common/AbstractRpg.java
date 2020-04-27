package cz.neumimto.rpg.common;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.exp.ExperienceService;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.api.utils.rng.PseudoRandomDistribution;
import cz.neumimto.rpg.common.commands.ACFBootstrap;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class AbstractRpg implements RpgApi {

    private final String workingDirectory;

    @Inject
    private EventFactoryService eventFactory;
    @Inject
    private SkillService skillService;
    @Inject
    private LocalizationService localizationService;
    //@Inject
    private PluginConfig pluginConfig;
    @Inject
    private DamageService damageService;
    @Inject
    private EffectService effectService;
    @Inject
    private ClassService classService;
    @Inject
    private ItemService itemService;
    @Inject
    private InventoryService inventoryService;
    @Inject
    private IScriptEngine scriptEngine;
    @Inject
    private PartyService partyService;
    @Inject
    private PropertyService propertyService;
    @Inject
    private EntityService entityService;
    @Inject
    private ResourceLoader resourceLoader;
    @Inject
    private CharacterService characterService;
    @Inject
    private ExperienceService experienceService;
    @Inject
    private Injector injector;
    @Inject
    private Gui gui;

    protected Executor currentThreadExecutor;

    public AbstractRpg(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public ItemService getItemService() {
        return itemService;
    }

    @Override
    public void broadcastLocalizableMessage(String message, Arg arg) {
        broadcastMessage(localizationService.translate(message, arg));
    }

    @Override
    public void broadcastLocalizableMessage(String message, String name, String localizableName) {
        broadcastMessage(localizationService.translate(message, name, localizableName));
    }

    @Override
    public EventFactoryService getEventFactory() {
        return eventFactory;
    }

    @Override
    public SkillService getSkillService() {
        return skillService;
    }

    @Override
    public LocalizationService getLocalizationService() {
        return localizationService;
    }

    @Override
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public CharacterService getCharacterService() {
        return characterService;
    }

    @Override
    public EntityService getEntityService() {
        return entityService;
    }

    @Override
    public DamageService getDamageService() {
        return damageService;
    }

    @Override
    public PropertyService getPropertyService() {
        return propertyService;
    }

    @Override
    public PartyService getPartyService() {
        return partyService;
    }

    @Override
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public ClassService getClassService() {
        return classService;
    }

    @Override
    public EffectService getEffectService() {
        return effectService;
    }

    @Override
    public IScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    @Override
    public InventoryService getInventoryService() {
        return inventoryService;
    }

    @Override
    public ExperienceService getExperienceService() {
        return experienceService;
    }

    @Override
    public void reloadMainPluginConfig() {
        File file = new File(getWorkingDirectory());
        if (!file.exists()) {
            file.mkdir();
        }
        File properties = new File(getWorkingDirectory(), "Settings.conf");
        if (!properties.exists()) {
            FileUtils.generateConfigFile(new PluginConfig(), properties);
        }

        try (FileConfig fileConfig = FileConfig.of(properties.getPath())) {
            fileConfig.load();
            PluginConfig pluginConfig = new PluginConfig();
            this.pluginConfig = new ObjectConverter().toObject(fileConfig, () -> pluginConfig);

            List<Map.Entry<String, ClassTypeDefinition>> list = new ArrayList<>(this.pluginConfig.CLASS_TYPES.entrySet());
            list.sort(Map.Entry.comparingByValue());

            Map<String, ClassTypeDefinition> result = new LinkedHashMap<>();
            for (Map.Entry<String, ClassTypeDefinition> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }
            this.pluginConfig.CLASS_TYPES = result;
        }
    }

    @Override
    public void init(Path workingDirPath, Object commandManager,
                     Class[] commandClasses, RpgAddon defaultStorageImpl,
                     BiFunction<Map, Map<Class<?>, ?>, Module> fnInjProv,
                     Consumer<Injector> injectorc) {
        reloadMainPluginConfig();

        PseudoRandomDistribution p = new PseudoRandomDistribution();
        PseudoRandomDistribution.C = new double[101];
        int a = 0;
        for (double i = 0.01; i <= 1; i += 0.01, a++) {
            PseudoRandomDistribution.C[a] = p.c(i);
        }

        AddonScanner.setDeployedDir(workingDirPath.resolve(".deployed"));
        AddonScanner.setAddonDir(workingDirPath.resolve("addons"));

        AddonScanner.prepareAddons();
        Set<RpgAddon> rpgAddons = ResourceManagerImpl.discoverGuiceModules();
        AddonScanner.onlyReloads();

        Map<Class<?>, Class<?>> bindings = new HashMap<>(defaultStorageImpl.getBindings());
        for (RpgAddon rpgAddon : rpgAddons) {
            bindings.putAll(rpgAddon.getBindings());
        }

        Map<Class<?>, ?> providers = new HashMap<>();
        Set<Class<?>> classesToLoad = AddonScanner.getClassesToLoad();
        Iterator<Class<?>> iterator = classesToLoad.iterator();
        try {
            while (iterator.hasNext()) {
                Class<?> next = iterator.next();
                if (RpgAddon.class.isAssignableFrom(next)) {
                    try {
                        RpgAddon addon = (RpgAddon) next.getConstructor().newInstance();
                        bindings.putAll(addon.getBindings());
                        Map<String, Object> map = new HashMap<>();
                        map.put("WORKINGDIR", workingDirPath.toAbsolutePath().toString());
                        providers = addon.getProviders(map); //TODO something definitely wrong with this
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            injector = Guice.createInjector(fnInjProv.apply(bindings, providers));
        } catch (Exception e) {
            Log.error("Could not create Guice Injector", e);
            return;
        }
        injectorc.accept(injector);

        URL pluginUrl = FileUtils.getPluginUrl();
        File file = new File(pluginUrl.getFile());
        getResourceLoader().loadJarFile(file, true);

        for (RpgAddon rpgAddon : rpgAddons) {
            rpgAddon.processStageEarly(injector);
        }

        getResourceLoader().initializeComponents();
        Locale locale = Locale.forLanguageTag(pluginConfig.LOCALE);
        try {
            getResourceLoader().reloadLocalizations(locale);
        } catch (Exception e) {
            Log.error("Could not read localizations in locale " + locale.toString() + " - " + e.getMessage());
        }

        getItemService().load();
        getInventoryService().load();
        getEventFactory().registerEventProviders();
        getExperienceService().load();

        getPropertyService().load();

        getScriptEngine().initEngine();
        getSkillService().load();
        getClassService().load();
        getEffectService().load();
        getEffectService().startEffectScheduler();
        getDamageService().init();

        List<BaseCommand> commands = new ArrayList<>();
        for (Class commandClass : commandClasses) {
            Object instance = injector.getInstance(commandClass);
            commands.add((BaseCommand) instance);
        }

        ACFBootstrap.initializeACF(((CommandManager) commandManager), commands);

        for (RpgAddon rpgAddon : rpgAddons) {
            rpgAddon.processStageLate(injector);
        }
    }

    @Override
    public Executor getSyncExecutor() {
        return currentThreadExecutor;
    }

    @Override
    public boolean isDisabledInWorld(String worldName) {
        return pluginConfig.DISABLED_WORLDS.contains(worldName);
    }
}
