package cz.neumimto.rpg;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.scripting.ScriptExecutorSkill;
import cz.neumimto.rpg.common.scripting.JSLoader;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import javax.inject.Inject;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class AssetsLoadingTest {

    @Inject
    private SkillService skillService;

    @Inject
    private JSLoader jsLoader;

    @Inject
    private Injector injector;

    @BeforeEach
    public void beforeEach() throws Exception {
        NtRpgPlugin.GlobalScope = new GlobalScope();
        NtRpgPlugin.GlobalScope.skillService = (SpongeSkillService) injector.getInstance(SkillService.class);
        jsLoader.initEngine();
        Bindings bindings = JSLoader.getEngine().getBindings(ScriptContext.GLOBAL_SCOPE);
        bindings = bindings == null ? new SimpleBindings() : bindings;
        bindings.put("ScriptExecutorSkill", ScriptExecutorSkill.class);
        JSLoader.getEngine().eval("var ScriptExecutorSkill = Java.type(\"cz.neumimto.rpg.api.skills.scripting.ScriptExecutorSkill\")");
        JSLoader.getEngine().setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        skillService.init();
    }

    @Test
    public void testJSSkillLoading() throws URISyntaxException {
        File file = new File(getClass().getClassLoader().getResource("testconfig/Skills-Definition.conf").getFile());
        jsLoader.loadSkillDefinitionFile(new URLClassLoader(new URL[]{}, this.getClass().getClassLoader()), file);
        Assertions.assertTrue(skillService.getById("ntrpg:jstest").isPresent());
    }
}
