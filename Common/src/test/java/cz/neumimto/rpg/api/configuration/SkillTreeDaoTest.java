package cz.neumimto.rpg.api.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.common.configuration.SkillTreeLoaderImpl;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.api.skills.types.AbstractSkill;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(NtRpgExtension.class)
class SkillTreeDaoTest {

    @BeforeAll
    public static void beforeAll() {
        NtRpgPlugin.GlobalScope = new GlobalScope();
        NtRpgPlugin.GlobalScope.skillService = new SpongeSkillService();
        AbstractSkill spy = Mockito.spy(AbstractSkill.class);
        Mockito.doNothing().when(spy).init();
        Mockito.when(spy.getName()).thenReturn("test");
        spy.setDescription(Arrays.asList("test"));
        spy.setCatalogId("test");
        NtRpgPlugin.GlobalScope.skillService.registerAdditionalCatalog(spy);

        spy = Mockito.spy(AbstractSkill.class);
        Mockito.doNothing().when(spy).init();
        Mockito.when(spy.getName()).thenReturn("test2");
        spy.setDescription(Arrays.asList("test"));
        spy.setCatalogId("test2");
        NtRpgPlugin.GlobalScope.skillService.registerAdditionalCatalog(spy);
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