package cz.neumimto.rpg;

import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.CharactersExtension.Stage;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.sponge.permission.TestPermissionService;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cz.neumimto.rpg.junit.CharactersExtension.Stage.Stages.READY;

@ExtendWith({CharactersExtension.class, GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
public class ClassServiceTest {

    @Inject
    private ClassService classService;

    @Inject
    private PermissionService permissionService;

    private ClassDefinition classDefinition;

    private ClassDefinition classDefinition2;

    @BeforeEach
    public void before() {
        classDefinition = new ClassDefinition("Test", "TestType");
        classService.registerClassDefinition(classDefinition);
        classDefinition2 = new ClassDefinition("Test" + TestPermissionService.MISSING_PERM, "TestType");
        classService.registerClassDefinition(classDefinition2);

    }

    @Test
    public void testClassLoopkup() {
        Assertions.assertTrue(classService.existsClass("TesT"));
        ClassDefinition test = classService.getClassDefinitionByName("Test");
        Assertions.assertSame(test, classDefinition);
        test = classService.getClassDefinitionByName("TeSt");
        Assertions.assertSame(test, classDefinition);
    }

    @Test
    public void testFiltering(@Stage(READY) IActiveCharacter character) {
        Set<ClassDefinition> testType = classService.filterByPlayerAndType(character, "TestType");
        Assertions.assertTrue(testType.contains(classDefinition));
        Assertions.assertFalse(testType.contains(classDefinition2));
    }

    @Test
    public void testLoadClasses() {
        ClassDefinitionDao dao = new ClassDefinitionDao() {
            @Override
            public Set<ClassDefinition> parseClassFiles() {
                return new HashSet<>(Arrays.asList(classDefinition, classDefinition2));
            }
        };
        ReflectionTestUtils.set(classService, "classDefinitionDao", dao);
        classService.load();
        Assertions.assertSame(classService.getClasses().size(), 2);
    }
}
