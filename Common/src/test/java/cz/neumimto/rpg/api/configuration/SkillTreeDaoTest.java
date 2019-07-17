package cz.neumimto.rpg.api.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.RpgTest;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.types.AbstractSkill;
import cz.neumimto.rpg.common.configuration.SkillTreeLoaderImpl;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
class SkillTreeDaoTest {

    @Inject
    private SkillService skillService;


    @Inject
    private RpgApi api;

    @BeforeEach
    public void before() {
        new RpgTest(api);
        AbstractSkill spy = Mockito.spy(AbstractSkill.class);
        Mockito.doNothing().when(spy).init();
        Mockito.when(spy.getName()).thenReturn("test");
        spy.setDescription(Arrays.asList("test"));
        spy.setCatalogId("test");
        skillService.registerAdditionalCatalog(spy);

        spy = Mockito.spy(AbstractSkill.class);
        Mockito.doNothing().when(spy).init();
        Mockito.when(spy.getName()).thenReturn("test2");
        spy.setDescription(Arrays.asList("test"));
        spy.setCatalogId("test2");
        skillService.registerAdditionalCatalog(spy);
    }

    @Test
    void loadSkillConfigTest01() {
        Map<String, SkillTree> skillTreeMap = new HashMap<>();
        Config config = ConfigFactory.load(getClass().getClassLoader(), "testconfig/SkillTree01.conf");
        new SkillTreeLoaderImpl().populateMap(skillTreeMap, config);
        Assertions.assertTrue(skillTreeMap.containsKey("Test"));
        Assertions.assertTrue(skillTreeMap.get("Test").getSkills().containsKey("test"));
        Assertions.assertTrue(skillTreeMap.get("Test").getSkills().containsKey("test2"));

        SkillTree test2 = skillTreeMap.get("Test");
        Assertions.assertTrue(test2.getSkillById("test2").getDescription(null).get(0).equalsIgnoreCase("asdf"));
        Assertions.assertTrue(test2.getSkillById("test").getDescription(null).get(0).equalsIgnoreCase("test"));
    }
}