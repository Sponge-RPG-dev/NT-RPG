package cz.neumimto.rpg;

import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.entity.IPropertyService;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.exp.IExperienceService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.common.impl.TestCharacterService;
import cz.neumimto.rpg.common.inventory.TestInventoryService;
import cz.neumimto.rpg.entity.TestEntityService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private IResourceLoader resourceLoader;

    @Inject
    private ClassService classService;

    @Inject
    private IEffectService effectService;

    @Inject
    private IScriptEngine jsLoader;

    @Inject
    private TestEntityService entityService;

    @Inject
    private DamageService damageService;

    @Inject
    private IPropertyService propertyService;

    @Inject
    private PartyService partyService;

    @Inject
    private IExperienceService experienceService;

    @Override
    public IResourceLoader getResourceLoader() {
        return resourceLoader;
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
    public IPropertyService getPropertyService() {
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
        String string = this.getClass().getClassLoader().getResource("classes").toString();
        return new File(string).getParent().substring(6);
    }


    @Override
    public TestInventoryService getInventoryService() {
        return null;
    }

    @Override
    public IExperienceService getExperienceService() {
        return experienceService;
    }

    @Override
    public void reloadMainPluginConfig() {

    }

    @Override
    public void postInit(CommandManager manager) {

    }

    @Override
    public void scheduleSyncLater(Runnable runnable) {
        runnable.run();
    }
}
