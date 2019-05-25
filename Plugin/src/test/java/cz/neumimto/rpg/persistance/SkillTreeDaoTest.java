package cz.neumimto.rpg.persistance;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.GlobalScope;
import cz.neumimto.rpg.common.configuration.SkillTreeDao;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.api.skills.types.AbstractSkill;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

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
        spy.setCatalogId("test");
        NtRpgPlugin.GlobalScope.skillService.registerAdditionalCatalog(spy);
    }

    @Test
    void loadSkillConfigTest01() {
        Map<String, SkillTree> skillTreeMap = new HashMap<>();
        Config config = ConfigFactory.load(getClass().getClassLoader(), "testconfig/SkillTree01.conf");
        new SkillTreeDao().populateMap(skillTreeMap, config);
        skillTreeMap.containsKey("test");
    }
}