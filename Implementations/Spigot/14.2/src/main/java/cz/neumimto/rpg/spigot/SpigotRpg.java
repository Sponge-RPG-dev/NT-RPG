package cz.neumimto.rpg.spigot;

import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.IEffectService;
import cz.neumimto.rpg.api.entity.IPropertyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.spigot.entities.SpigotEntityService;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.entities.players.party.SpigotPartyService;
import cz.neumimto.rpg.spigot.inventory.SpigotInventoryService;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Singleton
public class SpigotRpg implements RpgApi {

    @Inject
    private EventFactoryService spigotEventFactory;

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
    private SpigotInventoryService inventoryService;

    @Inject
    private IScriptEngine scriptEngine;

    @Inject
    private SpigotPartyService spigotPartyService;

    @Inject
    private IPropertyService propertyService;

    @Inject
    private SpigotEntityService spigotEntityService;

    @Inject
    private IResourceLoader iresourceLoader;

    @Inject
    private SpigotCharacterService characterService;

    private final String workingDirectory;

    public SpigotRpg(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }


    @Override
    public ItemService getItemService() {
        return itemService;
    }

    @Override
    public void broadcastMessage(String message) {
        Bukkit.broadcastMessage(message);
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
    public String getTextAssetContent(String templateName) {
        return null;
    }

    @Override
    public void executeCommandBatch(Map<String, String> args, List<String> enterCommands) {

    }

    @Override
    public boolean postEvent(Object event) {
        Bukkit.getServer().getPluginManager().callEvent((Event) event);
        if (event instanceof Cancellable) {
            return ((Cancellable) event).isCancelled();
        }
        return true;
    }

    @Override
    public void unregisterListeners(Object listener) {
        HandlerList.unregisterAll((Listener) listener);
    }

    @Override
    public void registerListeners(Object listener) {
        Bukkit.getServer().getPluginManager().registerEvents((Listener) listener, SpigotRpgPlugin.getInstance());
    }

    @Override
    public EventFactoryService getEventFactory() {
        return spigotEventFactory;
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
        return null;
    }

    @Override
    public SpigotCharacterService getCharacterService() {
        return characterService;
    }

    @Override
    public SpigotEntityService getEntityService() {
        return spigotEntityService;
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
    public SpigotPartyService getPartyService() {
        return spigotPartyService;
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
    public SpigotInventoryService getInventoryService() {
        return inventoryService;
    }
}
