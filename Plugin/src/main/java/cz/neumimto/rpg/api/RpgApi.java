package cz.neumimto.rpg.api;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RpgApi {

    Collection<Attribute> getAttributes();

    Optional<Attribute> getAttributeById(String id);

    Optional<SkillPreProcessorFactory> getSkillPreProcessorFactory(String preprocessorFactoryId);

    ItemService getItemService();

    void broadcastMessage(String message);

    void broadcastLocalizableMessage(String message, Arg arg);

    void broadcastLocalizableMessage(String playerLearnedSkillGlobalMessage, String name, String localizableName);

    String getTextAssetContent(String templateName);

    void executeCommandBatch(Map<String, String> args, List<String> enterCommands);

    boolean postEvent(Object event);
}
