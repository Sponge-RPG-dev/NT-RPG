package cz.neumimto.rpg;

import com.google.inject.Injector;
import com.google.inject.Module;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.RpgAddon;
import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.configuration.PluginConfig;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.parties.PartyService;
import cz.neumimto.rpg.common.events.EventFactoryService;
import cz.neumimto.rpg.common.exp.ExperienceService;
import cz.neumimto.rpg.common.impl.TestCharacterService;
import cz.neumimto.rpg.common.inventory.TestInventoryService;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.scripting.NTScriptEngine;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.entity.TestEntityService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Singleton
public class TestApiImpl implements RpgApi {

    @Inject
    private ItemService itemService;

    @Inject
    private EventFactoryService eventFactoryService;

    @Inject
    private SkillService skillService;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private PluginConfig pluginConfig;

    @Inject
    private TestCharacterService testCharacterService;

    @Inject
    private ResourceLoader resourceLoader;

    @Inject
    private ClassService classService;

    @Inject
    private EffectService effectService;

    @Inject
    private NTScriptEngine ntScriptEngine;

    @Inject
    private TestEntityService entityService;

    @Inject
    private DamageService damageService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private PartyService partyService;

    @Inject
    private ExperienceService experienceService;

    @Inject
    private Injector injector;

    @Inject
    private ResourceService resourceService;

    @Inject
    private PermissionService permissionService;

    public static List<Object> listeners = new ArrayList<>();

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
        return ntScriptEngine;
    }

    @Override
    public TestEntityService getEntityService() {
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
    public ItemService getItemService() {
        return itemService;
    }

    @Override
    public void broadcastMessage(String message) {

    }

    @Override
    public void broadcastLocalizableMessage(String message, Arg arg) {

    }

    @Override
    public void broadcastLocalizableMessage(String playerLearnedSkillGlobalMessage, String name, String localizableName) {

    }

    @Override
    public String getTextAssetContent(String templateName) {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(templateName);
        return new Scanner(resourceAsStream, "utf-8").useDelimiter("\\Z").next();
    }

    @Override
    public void executeCommandBatch(Map<String, String> args, List<String> enterCommands) {

    }

    @Override
    public void executeCommandAs(UUID sender, Map<String, String> args, List<String> enterCommands) {

    }

    @Override
    public boolean postEvent(Object event) {
        TestEventBus.BUS.add(event);
        return false;
    }

    @Override
    public void unregisterListeners(Object listener) {
        listeners.remove(listener);
    }

    @Override
    public void registerListeners(Object listener) {
        listeners.add(listener);
    }

    @Override
    public EventFactoryService getEventFactory() {
        return eventFactoryService;
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
    public Executor getAsyncExecutor() {
        return Executors.newFixedThreadPool(2);
    }

    @Override
    public CharacterService getCharacterService() {
        return testCharacterService;
    }

    @Override
    public String getWorkingDirectory() {
        Path path = Paths.get("./build/tmp/tests/");
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path.toString();
    }


    @Override
    public TestInventoryService getInventoryService() {
        return null;
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

    }

    @Override
    public void scheduleSyncLater(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void scheduleSyncLater(long millis, Runnable runnable) {

    }

    @Override
    public void init(Path workingDirPath, Object commandManager, Collection commandClasses, RpgAddon defaultStorageImpl, BiFunction<Map, Map<Class<?>, ?>, Module> fnInjProv, Consumer<Injector> injectorc) {

    }

    @Override
    public Executor getSyncExecutor() {
        return Runnable::run;
    }

    @Override
    public boolean isDisabledInWorld(String worldName) {
        return false;
    }

    @Override
    public Set<UUID> getOnlinePlayers() {
        return null;
    }

    @Override
    public void doImplSpecificreload() {

    }

    @Override
    public String getPlatform() {
        return "Test";
    }

    @Override
    public ResourceService getResourceService() {
        return resourceService;
    }
}
