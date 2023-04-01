package cz.neumimto.rpg;

import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.common.commands.AdminCommands;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.IGlobalEffect;
import cz.neumimto.rpg.common.entity.CommonProperties;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.entity.players.leveling.Linear;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.gui.IPlayerMessage;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.persistance.model.CharacterClass;
import cz.neumimto.rpg.effects.TestEffectFloat;
import cz.neumimto.rpg.effects.TestEffectFloatGlobal;
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

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
public class AdminCommandTests {

    @Inject
    private AdminCommands abstractAdminCommand;

    @Inject
    private EffectService effectService;

    @Inject
    private IPlayerMessage vanillaMessaging;

    @Inject
    private RpgApi rpgApi;

    @BeforeEach
    public void before() {
        new RpgTest(rpgApi);
        new Gui(vanillaMessaging);
    }

    @Test
    public void testAddEffectCommand(@Stage(READY) ActiveCharacter ActiveCharacter) {
        effectService.registerGlobalEffect(new TestEffectFloatGlobal());

        IGlobalEffect globalEffect = effectService.getGlobalEffect(TestEffectFloat.name);
        abstractAdminCommand.commandAddEffectToPlayer("10", globalEffect, 2000, ActiveCharacter);
        Assertions.assertTrue(ActiveCharacter.hasEffect(TestEffectFloat.name));
    }

    @Test
    public void testAddExpCommand(@Stage(READY) ActiveCharacter ActiveCharacter) {
        ClassDefinition classDefinition = new ClassDefinition("test", "test");


        Linear linear = new Linear() {
            @Override
            public int getMaxLevel() {
                return 5;
            }

            @Override
            public double[] getLevelMargins() {
                return new double[]{100.D, 200, 300, 400, 500};
            }
        };
        linear.initCurve();

        try {
            TestHelper.setField(classDefinition, "levels", linear);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CharacterClass jpaCharacterClass = new CharacterClass();
        jpaCharacterClass.setLevel(0);
        jpaCharacterClass.setExperiences(0);
        PlayerClassData data = new PlayerClassData(classDefinition, jpaCharacterClass) {
            @Override
            public boolean takesExp() {
                return true;
            }
        };
        ActiveCharacter.addClass(data);
        classDefinition.addExperienceSource("expSourceTest".toUpperCase());

        ActiveCharacter.setProperty(CommonProperties.experiences_mult, 1);
        abstractAdminCommand.commandAddExperiences(ActiveCharacter, 10D, "expSourceTest");
        Assertions.assertEquals(jpaCharacterClass.getExperiences(), 10D);
    }

    @Test
    public void testAddUniqueSkillPointsWrongArgs3(@Stage(READY) ActiveCharacter ActiveCharacter) {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            abstractAdminCommand.commandAddUniqueSkillpoint(ActiveCharacter, "Primary", null);
        });
    }

    @Test
    public void testAddUniqueSkillPoints(@Stage(READY) ActiveCharacter ActiveCharacter) {
        PlayerClassData primary = ActiveCharacter.getClassByType("Primary");
        CharacterBase characterBase = ActiveCharacter.getCharacterBase();

        CharacterClass characterClass = characterBase.getCharacterClass(primary.getClassDefinition());
        int i = characterClass.getSkillPoints();

        abstractAdminCommand.commandAddUniqueSkillpoint(ActiveCharacter, "Primary", "testing");

        Assertions.assertEquals(characterClass.getSkillPoints(), i + 1);
        Assertions.assertEquals(characterBase.getUniqueSkillpoints().size(), 1);
    }

}
