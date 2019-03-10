package cz.neumimto.rpg;

import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.ActionResult;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillDependency;
import cz.neumimto.rpg.skills.tree.SkillTree;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventManager;

import java.util.UUID;

import static org.mockito.Matchers.any;

public class SkillManipulationTests {

    private CharacterService characterService = new CharacterService();

    ISkill main;
    ISkill conflicting;
    ISkill hardDepending2;
    ISkill hardDepending1;
    ISkill softDepending0;
    ISkill softDepending1;

    ClassDefinition classDefinition;

    CharacterClass characterClass;

    CharacterBase characterBase;

    SkillData skillData;
    SkillData sconflicting;
    SkillData shardDepending2;
    SkillData shardDepending1;
    SkillData ssoftDepending0;
    SkillData ssoftDepending1;

    ActiveCharacter character;
    PlayerClassData playerClassData;

    @BeforeClass
    public static void init() throws Exception {
        TestHelper.initLocalizations();
    }

    @Before
    public void before() throws Exception {
        Log.logger = Mockito.mock(Logger.class);

        //lets not invoke constructor
        PluginConfig o = (PluginConfig) TestHelper.getUnsafe().allocateInstance(PluginConfig.class);
        o.PRIMARY_CLASS_TYPE = "Primary";
        NtRpgPlugin.pluginConfig = o;
        main = TestHelper.createMockSkill("main");

        conflicting = TestHelper.createMockSkill("conflicting");
        hardDepending2 = TestHelper.createMockSkill("hardDepending2");
        hardDepending1 = TestHelper.createMockSkill("hardDepending1");
        softDepending0 = TestHelper.createMockSkill("softDepending0");
        softDepending1 = TestHelper.createMockSkill("softDepending1");

        classDefinition = TestHelper.createClassDefinition();

        characterClass = TestHelper.createCharacterClass();
        characterClass.setSkillPoints(1);
        characterClass.setUsedSkillPoints(1);
        characterClass.setLevel(0);

        skillData = new SkillData(main.getId());
        skillData.setSkill(main);


        sconflicting = new SkillData(conflicting.getId());
        sconflicting.setSkill(conflicting);


        shardDepending2 = new SkillData(hardDepending2.getId());
        shardDepending2.setSkill(hardDepending2);


        shardDepending1 = new SkillData(hardDepending1.getId());
        shardDepending1.setSkill(hardDepending1);


        ssoftDepending0 = new SkillData(softDepending0.getId());
        ssoftDepending0.setSkill(softDepending0);


        ssoftDepending1 = new SkillData(softDepending1.getId());
        ssoftDepending1.setSkill(softDepending1);

        skillData.getConflicts().add(sconflicting);
        skillData.getHardDepends().add(new SkillDependency(shardDepending1, 1));
        skillData.getHardDepends().add(new SkillDependency(shardDepending2, 2));
        skillData.getSoftDepends().add(new SkillDependency(ssoftDepending0, 1));
        skillData.getSoftDepends().add(new SkillDependency(ssoftDepending1, 2));



        TestHelper.setField(classDefinition, "skillTree", new SkillTree() {{
            getSkills().put(main.getId(), skillData);
            getSkills().put(conflicting.getId(), sconflicting);
            getSkills().put(hardDepending2.getId(), shardDepending2);
            getSkills().put(hardDepending1.getId(), shardDepending1);
            getSkills().put(softDepending0.getId(), ssoftDepending0);
            getSkills().put(softDepending1.getId(), ssoftDepending1);
        }});

        characterBase = new CharacterBase();
        characterBase.getCharacterClasses().add(characterClass);

        character = new ActiveCharacter(UUID.randomUUID(), characterBase);

        playerClassData = new PlayerClassData(classDefinition, characterClass);
        character.addClass(playerClassData);

        Game mock = Mockito.mock(Game.class);
        EventManager eventManager = Mockito.mock(EventManager.class);
        Mockito.when(mock.getEventManager()).thenReturn(eventManager);
        Mockito.when(eventManager.post(any(Event.class))).thenReturn(false);
        TestHelper.setField(characterService, "game", mock);
    }

    @Test
    public void learnSkill() throws Exception {
        characterService.learnSkill(character, playerClassData, main);

        Assert.assertEquals(0, characterClass.getSkillPoints());
        Assert.assertEquals(2, characterClass.getUsedSkillPoints());
        Assert.assertTrue(character.hasSkill(main.getId()));
        Assert.assertSame(character.getPrimaryClass(), playerClassData);
        Mockito.verify(main, Mockito.times(1)).skillLearn(Mockito.any());

    }

    @Test
    public void mayLearnSkill_NotEnoughSkillpoints() throws Exception {
        characterClass.setSkillPoints(0);
        ActionResult actionResult = characterService.canLearnSkill(character, classDefinition, main);

        Assert.assertTrue(!actionResult.isOk());
    }

    @Test
    public void mayLearnSkill_WrongClassSkillOrigin() throws Exception {
        ActionResult actionResult = characterService.canLearnSkill(character, new ClassDefinition("test", "Primary"), main);

        Assert.assertTrue(!actionResult.isOk());
    }

    @Test
    public void mayLearnSkill_WrongClassSkillBothNull() throws Exception {
        classDefinition.setSkillTree(null);
        ActionResult actionResult = characterService.canLearnSkill(character, new ClassDefinition("test", "Primary"), main);

        Assert.assertTrue(!actionResult.isOk());
    }

    @Test
    public void checkSkillSoftDependencies_all_ok() throws Exception {
        character.addSkill(ssoftDepending0.getSkillId(), new PlayerSkillContext(classDefinition, ssoftDepending0.getSkill()) {{
            setLevel(1);
        }});
        character.addSkill(ssoftDepending1.getSkillId(), new PlayerSkillContext(classDefinition, ssoftDepending1.getSkill()) {{
            setLevel(2);
        }});

        boolean result = characterService.hasSoftSkillDependencies(character, skillData);
        Assert.assertTrue(result);
    }

    @Test
    public void checkSkillSoftDependencies_one_ok() throws Exception {
        character.addSkill(ssoftDepending0.getSkillId(), new PlayerSkillContext(classDefinition, ssoftDepending0.getSkill()) {{
            setLevel(1);
        }});

        boolean result = characterService.hasSoftSkillDependencies(character, skillData);
        Assert.assertTrue(result);
    }

    @Test
    public void checkSkillSoftDependencies_none_ok() throws Exception {
        character.addSkill(shardDepending1.getSkillId(), new PlayerSkillContext(classDefinition, shardDepending1.getSkill()) {{
            setLevel(1);
        }});

        boolean result = characterService.hasSoftSkillDependencies(character, skillData);
        Assert.assertFalse(result);
    }

    @Test
    public void checkSkillSoftDependencies_empty() throws Exception {
        boolean result = characterService.hasSoftSkillDependencies(character, new SkillData(""));
        Assert.assertTrue(result);
    }

    @Test
    public void checkSkillHardDependencies_empty() throws Exception {
        boolean result = characterService.hasHardSkillDependencies(character, new SkillData(""));
        Assert.assertTrue(result);
    }

    @Test
    public void checkSkillHardDependencies_missing() throws Exception {
        boolean result = characterService.hasHardSkillDependencies(character, skillData);
        Assert.assertFalse(result);
    }

    @Test
    public void checkSkillHardDependencies_all_ok() throws Exception {
        character.addSkill(shardDepending1.getSkillId(), new PlayerSkillContext(classDefinition, shardDepending1.getSkill()) {{
            setLevel(1);
        }});
        character.addSkill(shardDepending2.getSkillId(), new PlayerSkillContext(classDefinition, shardDepending2.getSkill()) {{
            setLevel(2);
        }});
        boolean result = characterService.hasHardSkillDependencies(character, skillData);
        Assert.assertTrue(result);
    }

    @Test
    public void checkSkillHardDependencies_only_one() throws Exception {
        character.addSkill(shardDepending1.getSkillId(), new PlayerSkillContext(classDefinition, shardDepending1.getSkill()) {{
            setLevel(1);
        }});
        character.addSkill(shardDepending2.getSkillId(), new PlayerSkillContext(classDefinition, shardDepending2.getSkill()) {{
            setLevel(1);
        }});
        boolean result = characterService.hasHardSkillDependencies(character, skillData);
        Assert.assertFalse(result);
    }


    @Test
    public void checkSkillConflictingDependencies_empty() throws Exception {
        boolean result = !characterService.hasConflictingSkillDepedencies(character, new SkillData(""));
        Assert.assertTrue(result);
    }

    @Test
    public void checkSkillConflictingDependencies_has_conflicting_node() throws Exception {
        character.addSkill(sconflicting.getSkillId(), new PlayerSkillContext(classDefinition, sconflicting.getSkill()) {{
            setLevel(1);
        }});
        boolean result = characterService.hasConflictingSkillDepedencies(character, new SkillData(""));
        Assert.assertFalse(result);
    }

    @Test
    public void mayLearnSkill_ok() {
        character.addSkill(shardDepending1.getSkillId(), new PlayerSkillContext(classDefinition, shardDepending1.getSkill()) {{
            setLevel(1);
        }});
        character.addSkill(shardDepending2.getSkillId(), new PlayerSkillContext(classDefinition, shardDepending2.getSkill()) {{
            setLevel(2);
        }});
        character.addSkill(ssoftDepending1.getSkillId(), new PlayerSkillContext(classDefinition, ssoftDepending1.getSkill()) {{
            setLevel(2);
        }});
        ActionResult actionResult = characterService.canLearnSkill(character, classDefinition, main);

        Assert.assertTrue(actionResult.isOk());
    }


}
