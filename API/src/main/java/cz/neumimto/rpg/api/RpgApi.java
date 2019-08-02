package cz.neumimto.rpg.api;

import cz.neumimto.rpg.api.classes.ClassService;
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
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public interface RpgApi {

    void broadcastMessage(String message);

    void broadcastLocalizableMessage(String message, Arg arg);

    void broadcastLocalizableMessage(String playerLearnedSkillGlobalMessage, String name, String localizableName);

    String getTextAssetContent(String templateName);

    void executeCommandBatch(Map<String, String> args, List<String> enterCommands);

    boolean postEvent(Object event);

    void unregisterListeners(Object listener);

    void registerListeners(Object listener);

    ItemService getItemService();

    EventFactoryService getEventFactory();

    SkillService getSkillService();

    LocalizationService getLocalizationService();

    PluginConfig getPluginConfig();

    Executor getAsyncExecutor();

    ICharacterService getCharacterService();

    EntityService getEntityService();

    DamageService getDamageService();

    IPropertyService getPropertyService();

    PartyService getPartyService();

    String getWorkingDirectory();

    IResourceLoader getResourceLoader();

    ClassService getClassService();

    IEffectService getEffectService();

    IScriptEngine getScriptEngine();

    InventoryService getInventoryService();

    IExperienceService getExperienceService();

    void reloadMainPluginConfig();

    void postInit();
}
