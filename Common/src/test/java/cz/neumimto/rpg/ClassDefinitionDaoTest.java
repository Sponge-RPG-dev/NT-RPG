package cz.neumimto.rpg;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.assets.AssetService;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class, CharactersExtension.class})
@IncludeModule(TestGuiceModule.class)
public class ClassDefinitionDaoTest {

    @Inject
    private ClassDefinitionDao classDefinitionDao;

    @Inject
    private AssetService assetService;

    @Inject
    private ClassService classService;

    @BeforeEach
    public void before() {
        Path classDirectory = classDefinitionDao.getClassDirectory();
        classDirectory.resolve("class1.conf").toFile().delete();
        classDirectory.resolve("class2.conf").toFile().delete();
        assetService.copyToFile("classDependencyGraphTest/class1.conf",classDirectory.resolve("class1.conf"));
        assetService.copyToFile("classDependencyGraphTest/class2.conf",classDirectory.resolve("class2.conf"));

    }

    @Test
    public void test_class_def_ependencies_loading() {
        Rpg.get().getPluginConfig().CLASS_TYPES.put("primary", new ClassTypeDefinition());
        Path path = Paths.get(getClass().getClassLoader().getResource("assets/nt-rpg/classDependencyGraphTest").getPath());
        Set<ClassDefinition> classDefinitions = classDefinitionDao.parseClassFiles(path);

        boolean run = false;
        for (ClassDefinition classDefinition : classDefinitions) {
            if (classDefinition.getName().equalsIgnoreCase("class2")) {
                Set<ClassDefinition> softDepends = classDefinition.getClassDependencyGraph().getSoftDepends();
                Assertions.assertEquals(1, softDepends.size());
                Assertions.assertNotNull(softDepends.iterator().next());
                Assertions.assertTrue(softDepends.iterator().next().getName().equalsIgnoreCase("class1"));
                run =true;
            }
        }
        Assertions.assertTrue(run);
    }
}
