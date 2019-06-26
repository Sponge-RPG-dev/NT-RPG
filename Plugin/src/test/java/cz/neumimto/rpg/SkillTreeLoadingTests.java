package cz.neumimto.rpg;

import cz.neumimto.rpg.api.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillDependency;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith({NtRpgExtension.class, GuiceExtension.class})
@IncludeModule(TestGuiceModule.class)
public class SkillTreeLoadingTests {

    @Inject
    private SkillTreeDao skillTreeDao;

    @Inject
    private SkillService skillService;

    @Inject
    private IScriptEngine scriptEngine;

    @Inject
    private LocalizationService localizationService;


    @BeforeEach
    public void before() throws Exception {
        skillService.getSkills().put("test", new TestSkill("test"));
        skillService.getSkills().put("test2", new TestSkill("test2"));
        NtRpgPlugin.GlobalScope.skillService = skillService;
        NtRpgPlugin.GlobalScope.jsLoader = scriptEngine;
        NtRpgPlugin.GlobalScope.localizationService = localizationService;
        scriptEngine.loadNashorn();
    }

    @Test
    public void loadSkillTrees() {

        Map<String, SkillTree> all = skillTreeDao.getAll();

        Assertions.assertSame(all.size(), 1);

        SkillTree tree = all.values().iterator().next();

        Assertions.assertEquals(tree.getId(), "name");
        Assertions.assertEquals(tree.getDescription(), "test");

        SkillData sd = tree.getSkillById("test");
        Assertions.assertEquals(sd.getMaxSkillLevel(), 10);
        Assertions.assertEquals(sd.getCombination(), "LLR");
        Assertions.assertEquals(sd.getMinPlayerLevel(), 5);
        Assertions.assertEquals(sd.getLevelGap(), 2);

        Assertions.assertEquals(sd.getConflicts().iterator().next().getSkillId(), "test2");

        SkillDependency dependency = sd.getHardDepends().iterator().next();
        Assertions.assertEquals(dependency.skillData.getSkillId(), "test2");
        Assertions.assertEquals(dependency.minSkillLevel, 1);

        dependency = sd.getSoftDepends().iterator().next();
        Assertions.assertEquals(dependency.skillData.getSkillId(), "test2");
        Assertions.assertEquals(dependency.minSkillLevel, 1);

        TestCharacter testCharacter = new TestCharacter(UUID.randomUUID(), null, 1);
        List<String> description = sd.getDescription(testCharacter);
        Assertions.assertEquals(description.size(), 1);
        Assertions.assertEquals(description.get(0), "Contextualized Description: " + testCharacter.getUUID().toString());

        sd = tree.getSkills().get("test2");
        Assertions.assertEquals(sd.getDescription(testCharacter).get(0), "Simple Description");
    }

    private static class TestSkill extends ActiveSkill {

        public TestSkill(String id) {
            setCatalogId(id);
            setLocalizableName(id);
        }

        @Override
        public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext modifier) {

        }
    }
}
