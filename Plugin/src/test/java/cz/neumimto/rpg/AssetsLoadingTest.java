package cz.neumimto.rpg;

import com.google.inject.Injector;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.common.scripting.JSLoader;
import cz.neumimto.rpg.api.skills.ISkillService;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class AssetsLoadingTest {

    @Inject
    private ISkillService skillService;

    @Inject
    private JSLoader jsLoader;

    @Inject
    private Injector injector;

    @BeforeEach
    public void beforeEach() {
        NtRpgPlugin.GlobalScope = new GlobalScope();
        NtRpgPlugin.GlobalScope.skillService = (SpongeSkillService) injector.getInstance(ISkillService.class);
    }

    @Test
    public void testJSSkillLoading() throws URISyntaxException {
        File file = new File(getClass().getClassLoader().getResource("testconfig/Skills-Definition.conf").getFile());
        jsLoader.loadSkillDefinitionFile(new URLClassLoader(new URL[]{}, this.getClass().getClassLoader()), file);
    }
}
