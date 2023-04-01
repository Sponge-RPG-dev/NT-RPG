package cz.neumimto.rpg;

import com.google.inject.Injector;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.RpgApi;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillsDefinition;
import cz.neumimto.rpg.common.skills.scripting.ActiveScriptSkill;
import cz.neumimto.rpg.common.skills.scripting.ScriptEffectModel;
import cz.neumimto.rpg.common.skills.scripting.ScriptListenerModel;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@ExtendWith({GuiceExtension.class})
@IncludeModule(TestGuiceModule.class)
public class TestScriptingComplexUsecase {

    @Inject
    private RpgApi rpgApi;

    @Inject
    private Injector injector;

    @BeforeEach
    public void before() {
        new RpgTest(rpgApi);
        Rpg.get().getEventFactory().registerProvider(CharacterEvent.class, CharacterEvent::new);
        Log.setLogger(Logger.getLogger("test"));

    }

    public static class CharacterEvent {
        private ActiveCharacter character;

        public ActiveCharacter getCharacter() {
            return character;
        }

        public void setCharacter(ActiveCharacter character) {
            this.character = character;
        }
    }

    @Test
    public void testScriptFlow() throws Exception {

        SkillsDefinition skillsDefinition = new SkillsDefinition();

        var smodel = new ScriptSkillModel();
        smodel.id = "Skill1";
        smodel.handlerId = "nts";
        smodel.superType = "Active";
        smodel.script = """
                @num = 30
                @duration = 20
                                
                @effect = TestEffect{}
                @effect.duration = @duration
                @effect.consumer = @caster
                @effect.Num = @num
                add_effect{effect=@effect, es=@caster, source=@this_skill}
                                
                RETURN SkillResult.OK
                """;
        skillsDefinition.skills.add(smodel);


        var emodel = new ScriptEffectModel();
        emodel.fields = new HashMap<>();
        emodel.fields.put("Num", "numeric");
        emodel.id = "TestEffect";
        emodel.onApply = """
                    @effect.Num = 11
                    RETURN
                """;
        skillsDefinition.effects.add(emodel);

        var lmodel = new ScriptListenerModel();
        lmodel.id = "TestListener";
        lmodel.event = "CharacterEvent";
        lmodel.script = """
                    @character = @event.character
                    @effect = get_effect{e=@character,en="TestEffect"} as TestEffect
                    IF exists{o=@effect}
                        print{o=@effect}
                        @effect.Num=77
                        printd{d=@effect.Num}
                    END
                    RETURN
                """;
        skillsDefinition.listeners.add(lmodel);

        Rpg.get().getSkillService().loadSkillDefinitions(this.getClass().getClassLoader(), skillsDefinition);

        Optional<ISkill> byId = Rpg.get().getSkillService().getById(smodel.id);
        Assertions.assertTrue(byId.isPresent());

        ActiveScriptSkill iSkill = (ActiveScriptSkill) byId.get();

        TestCharacter testCharacter = new TestCharacter(UUID.randomUUID(), new CharacterBase(), 0);
        PlayerSkillContext playerSkillContext = new PlayerSkillContext(new ClassDefinition("", ""), iSkill, testCharacter);
        iSkill.cast(testCharacter, playerSkillContext);

        Assertions.assertTrue(testCharacter.getEffects().stream().anyMatch(a -> a.getName().equals(emodel.id)));

        Assertions.assertTrue(TestApiImpl.listeners.size() == 1);

        Object listener = TestApiImpl.listeners.get(0);

        CharacterEvent event = new CharacterEvent();
        event.setCharacter(testCharacter);

        Method m = Arrays.stream(listener.getClass().getDeclaredMethods()).findFirst().get();
        m.invoke(listener, event);

        IEffectContainer effect = testCharacter.getEffect(emodel.id);

    }


}
