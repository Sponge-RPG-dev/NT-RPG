package cz.neumimto.rpg;

import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.common.scripting.JSLoader;
import cz.neumimto.rpg.api.skills.ISkillService;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
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

    @Test
    public void testJSSkillLoading() throws URISyntaxException {
        File file = new File(getClass().getClassLoader().getResource("testconfig/Skills-Definition.conf").getFile());

        jsLoader.loadSkillDefinitionFile(new URLClassLoader(new URL[]{}, this.getClass().getClassLoader()), file);
    }
}
