package cz.neumimto.rpg.common;

import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IPropertyService;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.exp.IExperienceService;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.api.utils.rng.PseudoRandomDistribution;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public abstract class AbstractRpg implements RpgApi {

    @Inject
    private EventFactoryService eventFactory;

    @Inject
    private SkillService skillService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private PluginConfig pluginConfig;

    @Inject
    private DamageService damageService;

    @Inject
    private IEffectService effectService;

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
    private IPropertyService propertyService;

    @Inject
    private EntityService entityService;

    @Inject
    private IResourceLoader iresourceLoader;

    @Inject
    private ICharacterService characterService;

    @Inject
    private IExperienceService experienceService;
    
    private final String workingDirectory;

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
    public ICharacterService getCharacterService() {
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
    public IPropertyService getPropertyService() {
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
    public IResourceLoader getResourceLoader() {
        return iresourceLoader;
    }

    @Override
    public ClassService getClassService() {
        return classService;
    }

    @Override
    public IEffectService getEffectService() {
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
    public IExperienceService getExperienceService() {
        return experienceService;
    }

    public void reloadMainPluginConfig() {
        File file = new File(getWorkingDirectory());
        if (!file.exists()) {
            file.mkdir();
        }
        File properties = new File(getWorkingDirectory(), "Settings.conf");
        if (!properties.exists()) {
            FileUtils.generateConfigFile(new PluginConfig(), properties);
        }

        try {
            ObjectMapper<PluginConfig> mapper = ObjectMapper.forClass(PluginConfig.class);
            HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(properties.toPath()).build();
            pluginConfig = mapper.bind(new PluginConfig()).populate(hcl.load());

            List<Map.Entry<String, ClassTypeDefinition>> list = new ArrayList<>(pluginConfig.CLASS_TYPES.entrySet());
            list.sort(Map.Entry.comparingByValue());

            Map<String, ClassTypeDefinition> result = new LinkedHashMap<>();
            for (Map.Entry<String, ClassTypeDefinition> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }
            pluginConfig.CLASS_TYPES = result;
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postInit() {
        int a = 0;
        PseudoRandomDistribution p = new PseudoRandomDistribution();
        PseudoRandomDistribution.C = new double[101];
        for (double i = 0.01; i <= 1; i += 0.01) {
            PseudoRandomDistribution.C[a] = p.c(i);
            a++;
        }
        Locale locale = Locale.forLanguageTag(pluginConfig.LOCALE);
        try {
            getResourceLoader().reloadLocalizations(locale);
        } catch (Exception e) {
            Log.error("Could not read localizations in locale " + locale.toString() + " - " + e.getMessage());
        }
        getItemService().loadItemGroups(Paths.get(getWorkingDirectory()));
        getInventoryService().load();
        getEventFactory().registerEventProviders();
        getExperienceService().load();
        getSkillService().init();
        getPropertyService().init(Paths.get(getWorkingDirectory() + "/Attributes.conf"), Paths.get(getWorkingDirectory() + File.separator + "properties_dump.info"));
        getPropertyService().reLoadAttributes(Paths.get(getWorkingDirectory() + "/Attributes.conf"));
        getPropertyService().loadMaximalServerPropertyValues(Paths.get(getWorkingDirectory(), "max_server_property_values.properties"));
        getScriptEngine().initEngine();
        getSkillService().load();
        getClassService().loadClasses();
        getEffectService().load();
        getEffectService().startEffectScheduler();
        getDamageService().init();
    }

}
