package cz.neumimto.rpg.common.persistance.dao;

import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.inventory.SpongeItemService;
import cz.neumimto.rpg.sponge.properties.SpongePropertyService;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class ClassDefinitionDaoTest {

    @Inject
    private ClassDefinitionDao classDefinitionDao;

    @Inject
    private SpongeItemService itemService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private ClassService classService;

    @BeforeEach
    public void before() {
        propertyService.getAttributes().put(TestDictionary.STR.getId(), TestDictionary.STR);
        propertyService.getAttributes().put(TestDictionary.AGI.getId(), TestDictionary.AGI);
        NtRpgPlugin.GlobalScope.spongePropertyService = propertyService;
        NtRpgPlugin.GlobalScope.itemService = itemService;
        NtRpgPlugin.GlobalScope.classService = classService;
        propertyService.registerProperty("max_mana",1);
    }

    @Test
    void testClassConfigLoading() throws ObjectMappingException {
        Set<ClassDefinition> classDefinitions = classDefinitionDao.parseClassFiles();

    }
}