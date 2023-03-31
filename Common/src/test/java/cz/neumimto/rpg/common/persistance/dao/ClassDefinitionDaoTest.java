package cz.neumimto.rpg.common.persistance.dao;

import cz.neumimto.rpg.RpgTest;
import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.Set;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class ClassDefinitionDaoTest {

    @Inject
    private ClassDefinitionDao classDefinitionDao;

    @Inject
    private ItemService itemService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private ClassService classService;

    @Inject
    private RpgApi api;

    @BeforeEach
    public void before() {
        new RpgTest(api);
        propertyService.getAttributes().put(TestDictionary.STR.getId(), TestDictionary.STR);
        propertyService.getAttributes().put(TestDictionary.AGI.getId(), TestDictionary.AGI);
        propertyService.registerProperty("max_mana", 1);
    }

    @Test
    void testClassConfigLoading() {
        Path path = new File(getClass().getClassLoader().getResource("classes/group1").getFile()).toPath();
        Set<ClassDefinition> classDefinitions = classDefinitionDao.parseClassFiles(path);
        Assertions.assertSame(classDefinitions.size(), 1);
        ClassDefinition c = classDefinitions.iterator().next();
        Assertions.assertEquals(c.getName(), "ClassExample1");
        Assertions.assertEquals(c.getDescription().get(0), "Description");
        Assertions.assertEquals(c.getWelcomeMessage(), "WelcomeMessage");
        Assertions.assertEquals(c.getPreferedColor(), "PreferredTextColor");
        Assertions.assertEquals(c.getItemType(), "ItemType");
        Assertions.assertEquals(c.getClassType(), "Primary");
        Assertions.assertEquals(c.getItemType(), "ItemType");
        Assertions.assertEquals(c.getSkillpointsPerLevel(), 10);
        Assertions.assertEquals(c.getAttributepointsPerLevel(), 11);

    }
}