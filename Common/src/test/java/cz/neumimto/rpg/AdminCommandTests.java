package cz.neumimto.rpg;

import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.leveling.ILevelProgression;
import cz.neumimto.rpg.api.entity.players.leveling.Linear;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.gui.IPlayerMessage;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.common.commands.AdminCommandFacade;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.effects.TestEffectFloat;
import cz.neumimto.rpg.effects.TestEffectFloatGlobal;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.CharactersExtension.Stage;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.persistance.model.JPACharacterClass;
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
    private AdminCommandFacade adminCommandFacade;

    @Inject
    private EffectService effectService;

    @Inject
    private IPlayerMessage vanillaMessaging;

    @Inject
    private RpgApi rpgApi;

    @BeforeEach
    public void before(){
        new RpgTest(rpgApi);
        new Gui(vanillaMessaging);
    }

    @Test
    public void testAddEffectCommand(@Stage(READY)IActiveCharacter iActiveCharacter) {
        effectService.registerGlobalEffect(new TestEffectFloatGlobal());

        IGlobalEffect globalEffect = effectService.getGlobalEffect(TestEffectFloat.name);
        adminCommandFacade.commandAddEffectToPlayer("10", globalEffect, 2000, iActiveCharacter);
        Assertions.assertTrue(iActiveCharacter.hasEffect(TestEffectFloat.name));
    }

    @Test
    public void testAddExpCommand(@Stage(READY)IActiveCharacter iActiveCharacter) {
        ClassDefinition classDefinition = new ClassDefinition("test", "test") {
            @Override
            public ILevelProgression getLevelProgression() {
                Linear linear = new Linear() {
                    @Override
                    public int getMaxLevel() {
                        return 100;
                    }

                    @Override
                    public double[] getLevelMargins() {
                        return new double[] {100.D};
                    }
                };
                linear.initCurve();
                return linear;
            }
        };
        CharacterClass jpaCharacterClass = new JPACharacterClass();
        jpaCharacterClass.setLevel(0);
        jpaCharacterClass.setExperiences(0);
        PlayerClassData data = new PlayerClassData(classDefinition, jpaCharacterClass) {
            @Override
            public boolean takesExp() {
                return true;
            }
        };
        iActiveCharacter.addClass(data);
        classDefinition.addExperienceSource("expSourceTest");

        iActiveCharacter.setProperty(CommonProperties.experiences_mult, 1);
        adminCommandFacade.commandAddExperiences(iActiveCharacter, 10D, classDefinition, "expSourceTest");
        Assertions.assertEquals(jpaCharacterClass.getExperiences(), 10D);
    }
}
