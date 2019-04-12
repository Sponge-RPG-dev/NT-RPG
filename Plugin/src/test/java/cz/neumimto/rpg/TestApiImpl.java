package cz.neumimto.rpg;

import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class TestApiImpl implements cz.neumimto.rpg.api.RpgApi {
    @Override
    public Collection<Attribute> getAttributes() {
        return Arrays.asList(TestDictionary.AGI, TestDictionary.STR);
    }

    @Override
    public Optional<SkillPreProcessorFactory> getSkillPreProcessorFactory(String preprocessorFactoryId) {
        return Optional.empty();
    }

    @Override
    public ItemService getItemService() {
        return null;
    }
}
