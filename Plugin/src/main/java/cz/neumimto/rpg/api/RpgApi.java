package cz.neumimto.rpg.api;

import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactory;

import java.util.Collection;
import java.util.Optional;

public interface RpgApi {

    Collection<Attribute> getAttributes();

    Optional<SkillPreProcessorFactory> getSkillPreProcessorFactory(String preprocessorFactoryId);

    ItemService getItemService();

    void broadcastMessage(String message);

    void broadcastLocalizableMessage(String message, String[] keys, String[] args);

}
