package cz.neumimto.rpg.api;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.SkillService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public interface RpgApi {

    ItemService getItemService();

    void broadcastMessage(String message);

    void broadcastLocalizableMessage(String message, Arg arg);

    void broadcastLocalizableMessage(String playerLearnedSkillGlobalMessage, String name, String localizableName);

    String getTextAssetContent(String templateName);

    void executeCommandBatch(Map<String, String> args, List<String> enterCommands);

    boolean postEvent(Object event);

    void unregisterListeners(Object listener);

    void registerListeners(Object listener);

    EventFactoryService getEventFactory();

    SkillService getSkillService();

    LocalizationService getLocalizationService();

    PluginConfig getPluginConfig();

    Executor getAsyncExecutor();

    <C extends ICharacterService> C getCharacterService();

    <E extends EntityService> E getEntityService();

    DamageService getDamageService();

    <P extends PropertyService> P getPropertyService();

    <P extends PartyService> P getPartyService();

    String getWorkingDirectory();

    IResourceLoader getResourceLoader();
}
