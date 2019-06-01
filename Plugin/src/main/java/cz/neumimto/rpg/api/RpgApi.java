package cz.neumimto.rpg.api;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.api.events.effect.EventFactoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.messaging.MessageLevel;
import cz.neumimto.rpg.api.messaging.MessageProcessor;
import cz.neumimto.rpg.api.skills.ISkillService;
import cz.neumimto.rpg.api.skills.SkillPreProcessorFactory;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.sponge.configuration.PluginConfig;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RpgApi {

    Collection<Attribute> getAttributes();

    Optional<Attribute> getAttributeById(String id);

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

    ISkillService getSkillService();

    LocalizationService getLocalizationService();

    PluginConfig getPluginConfig();
}
