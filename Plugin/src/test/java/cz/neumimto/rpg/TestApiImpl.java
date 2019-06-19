package cz.neumimto.rpg;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.ISkillService;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacterServise;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import cz.neumimto.rpg.sponge.properties.SpongePropertyService;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestApiImpl implements RpgApi {

    @Override
    public ItemService getItemService() {
        return NtRpgPlugin.GlobalScope.itemService;
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
        return NtRpgPlugin.GlobalScope.eventFactory;
    }

    @Override
    public ISkillService getSkillService() {
        return NtRpgPlugin.GlobalScope.skillService;
    }

    @Override
    public LocalizationService getLocalizationService() {
        return NtRpgPlugin.GlobalScope.localizationService;
    }

    @Override
    public PluginConfig getPluginConfig() {
        return NtRpgPlugin.pluginConfig;
    }

    @Override
    public Executor getAsyncExecutor() {
        return Executors.newFixedThreadPool(2);
    }

    @Override
    public SpongeCharacterServise getCharacterService() {
        return NtRpgPlugin.GlobalScope.characterService;
    }


    @Override
    public SpongeEntityService getEntityService() {
        return NtRpgPlugin.GlobalScope.entityService;
    }

    @Override
    public DamageService getDamageService() {
        return NtRpgPlugin.GlobalScope.damageService;
    }

    @Override
    public SpongePropertyService getPropertyService() {
        return NtRpgPlugin.GlobalScope.spongePropertyService;
    }

    @Override
    public SpongePartyService getPartyService() {
        return NtRpgPlugin.GlobalScope.partyService;
    }

    @Override
    public String getWorkingDirectory() {
        return NtRpgPlugin.workingDir;
    }

    @Override
    public IResourceLoader getResourceLoader() {
        return NtRpgPlugin.GlobalScope.resourceLoader;
    }
}
