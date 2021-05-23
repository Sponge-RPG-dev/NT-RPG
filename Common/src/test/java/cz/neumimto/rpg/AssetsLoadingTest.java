package cz.neumimto.rpg;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.scripting.ScriptExecutorSkill;
import cz.neumimto.rpg.common.scripting.GraalVmScriptEngine;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class AssetsLoadingTest {

    @Inject
    private SkillService skillService;

    @Inject
    private GraalVmScriptEngine jsLoader;

    @Inject
    private Injector injector;

    @BeforeEach
    public void beforeEach() throws Exception {
        jsLoader = injector.getInstance(GraalVmScriptEngine.class);
        jsLoader.prepareEngine();
        skillService.load();
        skillService.registerSkillHandler("ntrpg:test", (SkillScriptHandlers.Active) (caster, context) -> SkillResult.OK);
    }

    @Test
    public void testJSSkillLoading() {
        File file = new File(getClass().getClassLoader().getResource("testconfig/Skills-Definition.conf").getFile());
        jsLoader.loadSkillDefinitionFile(new URLClassLoader(new URL[]{}, this.getClass().getClassLoader()), file);
        Assertions.assertTrue(skillService.getById("ntrpg:jstest").isPresent());
    }
}
