import cz.neumimto.ClassGenerator;
import cz.neumimto.ResourceLoader;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.ioc.IoC;
import cz.neumimto.persistance.GroupDao;
import cz.neumimto.persistance.SkillTreeDao;
import cz.neumimto.skills.SkillService;
import cz.neumimto.skills.SkillTree;
import javassist.CannotCompileException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.spongepowered.api.Game;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

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
