import cz.neumimto.ClassGenerator;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ResourceLoader;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.ioc.IoC;
import cz.neumimto.persistance.GroupDao;
import cz.neumimto.persistance.SkillTreeDao;
import cz.neumimto.players.ActiveCharacter;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.properties.DefaultProperties;
import cz.neumimto.players.properties.PlayerPropertyService;
import cz.neumimto.skills.SkillService;
import cz.neumimto.skills.SkillTree;
import javassist.CannotCompileException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.helpers.SubstituteLogger;
import org.spongepowered.api.Game;

import javax.persistence.EntityManager;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@RunWith(PowerMockRunner.class)
public class Tests {

    @Test
    public void testConfig() throws NoSuchFieldException, IllegalAccessException {
        ResourceLoader.raceDir = new File("./src/main/test/testfiles/races");
        ResourceLoader.guildsDir = new File("./src/main/test/testfiles/guilds");
        ResourceLoader.classDir = new File("./src/main/test/testfiles/classes");
        GroupDao dao = new GroupDao();
        dao.loadGuilds();
        dao.loadRaces();

        SkillService sk = mock(SkillService.class);
        when(sk.getSkillTrees()).thenReturn(new HashMap<String, SkillTree>() {{
            put("test",SkillTree.Default);
        }
        });
        Field f = dao.getClass().getDeclaredField("skillService");
        f.setAccessible(true);
        f.set(dao,sk);
        dao.loadNClasses();
        Assert.assertTrue(ResourceLoader.raceDir.listFiles().length == dao.getRaces().size());
        Assert.assertTrue(ResourceLoader.guildsDir.listFiles().length == dao.getGuilds().size());
        Assert.assertTrue(dao.getClasses().get("test").getLevels().length == 99);
    }

    @Test
    public void testHibernateConnection() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        EntityManager em = TestUtils.buildEntityManager();
        Assert.assertTrue(em != null);
        Assert.assertTrue(em.isOpen());
    }

    @Test
    public void testPropertyprocessor() {
        IoC ioC = IoC.get();
        ioC.registerInterfaceImplementation(Logger.class,new SubstituteLogger("test"));
        PlayerPropertyService service = ioC.build(PlayerPropertyService.class);
        service.process(DefaultProperties.class);
        int k = DefaultProperties.class.getFields().length;
        Assert.assertTrue(k == service.LAST_ID);
    }


    @Test
    public void testEffectClassGenerator() {
        ClassGenerator classGenerator = new ClassGenerator();
        EffectTest effectTest = new EffectTest();
        IGlobalEffect eff = null;
        try {
            eff = classGenerator.generateGlobalEffect(EffectTest.class);
            Assert.assertTrue(eff != null);
            classGenerator.injectGlobalEffectField(EffectTest.class,eff);
        } catch (CannotCompileException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(effectTest.global == eff);
    }
}
