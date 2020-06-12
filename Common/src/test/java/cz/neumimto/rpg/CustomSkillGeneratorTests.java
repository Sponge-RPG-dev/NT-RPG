package cz.neumimto.rpg;

import com.electronwill.nightconfig.core.Config;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.common.skills.scripting.Caster;
import cz.neumimto.rpg.common.skills.scripting.CustomSkillGenerator;
import cz.neumimto.rpg.common.skills.scripting.SkillArgument;
import cz.neumimto.rpg.common.skills.scripting.SkillMechanic;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ExtendWith({CharactersExtension.class, GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class CustomSkillGeneratorTests {

    @Inject
    private CustomSkillGenerator customSkillGenerator;

    private static AtomicBoolean atomicBoolean;

    @BeforeEach
    public static void before() {
        atomicBoolean = new AtomicBoolean(false);
    }

    @Test
    public void test() {
        ScriptSkillModel model = new ScriptSkillModel();
        model.setId("ntrpg:test");
        model.setHandlerId("custom");
        List<Config> list = new ArrayList<>();
        model.setSpell(list);
        Config config = Config.inMemory();
        config.set("Target-Selector", "testtarget");
        {
            Config c = Config.inMemory();

            Config inner = Config.inMemory();
            inner.set("Type", "ntrpg:test_mechanic");
            c.set("", inner);

            config.set("Mechanics", Collections.singletonList(c));
        }


        model.setSpell(list);
        customSkillGenerator.generate(model);
    }

    @SkillMechanic("ntrpg:test_mechanic")
    private static class TestMechanic {

        public void doAction(@Caster IActiveCharacter character, @SkillArgument("settings.node") float value) {

        }
    }
}
