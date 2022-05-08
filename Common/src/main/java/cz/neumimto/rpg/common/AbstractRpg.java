package cz.neumimto.rpg.common;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.commands.ACFBootstrap;
import cz.neumimto.rpg.common.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.common.configuration.PluginConfig;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.parties.PartyService;
import cz.neumimto.rpg.common.events.EventFactoryService;
import cz.neumimto.rpg.common.exp.ExperienceService;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.inventory.InventoryService;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.scripting.NTScriptEngine;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.common.utils.FileUtils;
import cz.neumimto.rpg.common.utils.rng.PseudoRandomDistribution;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private NTScriptEngine scriptEngine;
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
    private PermissionService permissionService;
    @Inject
    private Injector injector;
    @Inject
    private Gui gui;
    @Inject
    private ResourceService resourceService;

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
    public CharacterService<IActiveCharacter> getCharacterService() {
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
    public NTScriptEngine getScriptEngine() {
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
    public PermissionService getPermissionService() {
        return permissionService;
    }

    @Override
    public Injector getInjector() {
        return injector;
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

        resourceService.reload();

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

        AddonScanner.setAddonDir(workingDirPath.resolve("addons"));
        AddonScanner.prepareAddons();

        List<RpgAddon> rpgAddons = ServiceLoader.load(RpgAddon.class, this.getClass().getClassLoader())
                .stream().map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
        AddonScanner.onlyReloads();

        Map<Class<?>, Class<?>> bindings = new HashMap<>(defaultStorageImpl.getBindings());
        for (RpgAddon rpgAddon : rpgAddons) {
            bindings.putAll(rpgAddon.getBindings());
        }

        Map<Class<?>, ?> providers = new HashMap<>();

        try {
            for (RpgAddon addon : rpgAddons) {
                bindings.putAll(addon.getBindings());
                Map<String, Object> map = new HashMap<>();
                map.put("WORKINGDIR", workingDirPath.toAbsolutePath().toString());
                providers = addon.getProviders(map); //TODO something definitely wrong with this
            }
            injector = Guice.createInjector(fnInjProv.apply(bindings, providers));
        } catch (Exception e) {
            Log.error("Could not create Guice Injector", e);
            return;
        }
        injectorc.accept(injector);
        File file = FileUtils.getPluginFile(getPluginClass());

        for (RpgAddon rpgAddon : rpgAddons) {
            rpgAddon.processStageEarly(injector);
        }

        getResourceLoader().loadServices();
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

        doImplSpecificreload();
    }

    protected abstract Class getPluginClass();

    @Override
    public ResourceService getResourceService() {
        return resourceService;
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
