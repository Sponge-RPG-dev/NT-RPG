package cz.neumimto.rpg;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.events.effect.EventFactoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.skills.ISkillService;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.common.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.sponge.NtRpgPlugin;

import java.io.InputStream;
import java.util.*;

public class TestApiImpl implements RpgApi {

    @Override
    public Collection<AttributeConfig> getAttributes() {
        return Arrays.asList(TestDictionary.AGI, TestDictionary.STR);
    }

    @Override
    public Optional<AttributeConfig> getAttributeById(String id) {
        return Optional.empty();
    }

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
}
