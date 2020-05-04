package cz.neumimto.rpg;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.*;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.CharactersExtension.Stage;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({NtRpgExtension.class, GuiceExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
public class SkillExecutionTests {

    @Inject
    private SkillTreeDao skillTreeDao;

    @Inject
    private SkillService skillService;

    @Inject
    private IScriptEngine scriptEngine;

    @Inject
    private LocalizationService localizationService;

    @Inject
    private EventFactoryService eventFactoryService;

    @Inject
    private PropertyService propertyService;

    @Inject
    private EntityService entityService;

    @Inject
    private Injector injector;

    private TestSkill testSkill;

    protected static boolean hadRun;

    @BeforeEach
    public void before() {
        testSkill = injector.getInstance(TestSkill.class);
        skillService.getSkills().put("test", testSkill);
        hadRun = false;
    }

    @Test
    public void testBasicSkillExecution(@Stage(READY) IActiveCharacter character) {
        PlayerClassData primary = (PlayerClassData) character.getClasses().get("primary");
        PlayerSkillContext playerSkillContext = new PlayerSkillContext(primary.getClassDefinition(), testSkill, character);
        playerSkillContext.setSkill(testSkill);

        SkillData skillData = new SkillData(testSkill.getId());
        skillData.setSkillSettings(new SkillSettings());

        skillData.setSkill(testSkill);
        playerSkillContext.setSkillData(skillData);
        character.addSkill("test", playerSkillContext);
        Rpg.get().getSkillService().executeSkill(character, character.getSkill("test"), new SkillExecutorCallback() {
            @Override
            public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                super.doNext(character, info, skillResult);
                Assertions.assertSame(skillResult.getResult(), SkillResult.OK);
            }
        });
        Assertions.assertTrue(hadRun);
    }

    @Test
    public void testBasicSkillExecution_No_Mana(@Stage(READY) IActiveCharacter character) {
        PlayerClassData primary = (PlayerClassData) character.getClasses().get("primary");
        PlayerSkillContext playerSkillContext = new PlayerSkillContext(primary.getClassDefinition(), testSkill, character);
        playerSkillContext.setSkill(testSkill);

        SkillData skillData = new SkillData(testSkill.getId());
        skillData.setSkillSettings(new SkillSettings());
        skillData.getSkillSettings().addNode(SkillNodes.MANACOST, 100, 100);
        character.setProperty(CommonProperties.mana_cost_reduce, 1);
        skillData.setSkill(testSkill);
        playerSkillContext.setSkillData(skillData);
        character.addSkill("test", playerSkillContext);

        Rpg.get().getSkillService().executeSkill(character, character.getSkill("test"), new SkillExecutorCallback() {
            @Override
            public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                super.doNext(character, info, skillResult);
                Assertions.assertSame(skillResult.getResult(), SkillResult.NO_MANA);
            }
        });
        Assertions.assertFalse(hadRun);
    }

    @Test
    public void testBasicSkillExecution_No_Hp(@Stage(READY) IActiveCharacter character) {
        PlayerClassData primary = (PlayerClassData) character.getClasses().get("primary");
        PlayerSkillContext playerSkillContext = new PlayerSkillContext(primary.getClassDefinition(), testSkill, character);
        playerSkillContext.setSkill(testSkill);

        SkillData skillData = new SkillData(testSkill.getId());
        skillData.setSkillSettings(new SkillSettings());
        skillData.getSkillSettings().addNode(SkillNodes.HPCOST, 100, 100);
        character.setProperty(CommonProperties.health_cost_reduce, 1);
        skillData.setSkill(testSkill);
        playerSkillContext.setSkillData(skillData);
        character.addSkill("test", playerSkillContext);

        Rpg.get().getSkillService().executeSkill(character, character.getSkill("test"), new SkillExecutorCallback() {
            @Override
            public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                super.doNext(character, info, skillResult);
                Assertions.assertSame(skillResult.getResult(), SkillResult.NO_HP);
            }
        });
        Assertions.assertFalse(hadRun);
    }

    @Test
    public void testBasicSkillExecution_On_Cooldown(@Stage(READY) IActiveCharacter character) {
        PlayerClassData primary = (PlayerClassData) character.getClasses().get("primary");
        PlayerSkillContext playerSkillContext = new PlayerSkillContext(primary.getClassDefinition(), testSkill, character);
        playerSkillContext.setSkill(testSkill);

        SkillData skillData = new SkillData(testSkill.getId());
        skillData.setSkillSettings(new SkillSettings());
        character.getCooldowns().put(testSkill.getId(), Long.MAX_VALUE);
        character.setProperty(CommonProperties.health_cost_reduce, 1);
        skillData.setSkill(testSkill);
        playerSkillContext.setSkillData(skillData);
        character.addSkill("test", playerSkillContext);

        Rpg.get().getSkillService().executeSkill(character, character.getSkill("test"), new SkillExecutorCallback() {
            @Override
            public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext skillResult) {
                super.doNext(character, info, skillResult);
                Assertions.assertSame(skillResult.getResult(), SkillResult.ON_COOLDOWN);
            }
        });
        Assertions.assertFalse(hadRun);
    }


    @Test
    public void testBasicSkillExecution_Configured_Preprocessor(@Stage(READY) IActiveCharacter character) {
        PlayerClassData primary = (PlayerClassData) character.getClasses().get("primary");
        PlayerSkillContext playerSkillContext = new PlayerSkillContext(primary.getClassDefinition(), testSkill, character);
        playerSkillContext.setSkill(testSkill);

        SkillData skillData = new SkillData(testSkill.getId());
        AtomicBoolean runFirst = new AtomicBoolean(false);
        AtomicBoolean runLast = new AtomicBoolean(false);

        skillData.setSkillSettings(new SkillSettings());
        skillData.setSkill(testSkill);
        playerSkillContext.setSkillData(skillData);
        character.addSkill("test", playerSkillContext);

        Assertions.assertTrue(runFirst.get());
        Assertions.assertTrue(runLast.get());
        Assertions.assertTrue(hadRun);
    }

    private static class TestSkill extends ActiveSkill {

        public TestSkill() {
            String id = "test";
            setCatalogId(id);
        }

        @Override
        public SkillResult cast(IActiveCharacter character, PlayerSkillContext info) {
            hadRun = true;
            return SkillResult.OK;
        }
    }
}
