package cz.neumimto.rpg;

import com.google.inject.Injector;
import com.google.inject.Module;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.exp.ExperienceService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.scripting.IRpgScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.common.impl.TestCharacterService;
import cz.neumimto.rpg.common.inventory.TestInventoryService;
import cz.neumimto.rpg.entity.TestEntityService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
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
    private IRpgScriptEngine jsLoader;

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
    public IRpgScriptEngine getScriptEngine() {
        return jsLoader;
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

    }

    @Override
    public void registerListeners(Object listener) {

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
    public TestCharacterService getCharacterService() {
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
    public void reloadMainPluginConfig() {

    }

    @Override
    public void scheduleSyncLater(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void init(Path workingDirPath, Object commandManager, Class[] commandClasses, RpgAddon defaultStorageImpl, BiFunction<Map, Map<Class<?>, ?>, Module> fnInjProv, Consumer<Injector> injectorc) {

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
}
