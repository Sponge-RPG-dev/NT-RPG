package cz.neumimto.rpg;

import com.google.inject.Injector;
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

    private GraalVmScriptEngine jsLoader;

    @Inject
    private Injector injector;

    @Inject
    private TestApiImpl api;

    @BeforeEach
    public void beforeEach() throws Exception {
        jsLoader = injector.getInstance(GraalVmScriptEngine.class);
        new RpgTest(api);
        jsLoader.prepareEngine();
     //   Bindings bindings = jsLoader.getCompiledLib().getEngine().getBindings(ScriptContext.GLOBAL_SCOPE);
     //   bindings = bindings == null ? new SimpleBindings() : bindings;
    //    bindings.put("ScriptExecutorSkill", ScriptExecutorSkill.class);
     //   jsLoader.getCompiledLib().getEngine().eval("var ScriptExecutorSkill = Java.type(\"" + ScriptExecutorSkill.class.getCanonicalName() + "\")");
     //   jsLoader.getCompiledLib().getEngine().setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        skillService.load();
    }

    @Test
    public void testJSSkillLoading() throws URISyntaxException {
        File file = new File(getClass().getClassLoader().getResource("testconfig/Skills-Definition.conf").getFile());
        jsLoader.loadSkillDefinitionFile(new URLClassLoader(new URL[]{}, this.getClass().getClassLoader()), file);
        Assertions.assertTrue(skillService.getById("ntrpg:jstest").isPresent());
    }
}
