package cz.neumimto.rpg.api;

import com.google.inject.Injector;
import com.google.inject.Module;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.exp.ExperienceService;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface RpgApi {

    void broadcastMessage(String message);

    void broadcastLocalizableMessage(String message, Arg arg);

    void broadcastLocalizableMessage(String playerLearnedSkillGlobalMessage, String name, String localizableName);

    String getTextAssetContent(String templateName);

    void executeCommandBatch(Map<String, String> args, List<String> enterCommands);

    void executeCommandAs(UUID sender, Map<String, String> args, List<String> enterCommands);

    /**
     * @param event
     * @return True if cancelled
     */
    boolean postEvent(Object event);

    void unregisterListeners(Object listener);

    void registerListeners(Object listener);

    ItemService getItemService();

    EventFactoryService getEventFactory();

    SkillService getSkillService();

    LocalizationService getLocalizationService();

    PluginConfig getPluginConfig();

    Executor getAsyncExecutor();

    CharacterService getCharacterService();

    EntityService getEntityService();

    DamageService getDamageService();

    PropertyService getPropertyService();

    PartyService getPartyService();

    String getWorkingDirectory();

    ResourceLoader getResourceLoader();

    ClassService getClassService();

    EffectService getEffectService();

    IScriptEngine getScriptEngine();

    InventoryService getInventoryService();

    ExperienceService getExperienceService();

    void reloadMainPluginConfig();

    void scheduleSyncLater(Runnable runnable);

    void init(Path workingDirPath, Object commandManager, Class[] commandClasses, RpgAddon defaultStorageImpl,
              BiFunction<Map, Map<Class<?>, ?>, Module> fnInjProv, Consumer<Injector> injectorc);

    Executor getSyncExecutor();

    boolean isDisabledInWorld(String worldName);

    Set<UUID> getOnlinePlayers();

    void doImplSpecificreload();
}
