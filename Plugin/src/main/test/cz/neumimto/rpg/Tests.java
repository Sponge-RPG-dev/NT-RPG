package cz.neumimto.rpg;


import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.common.positive.SpeedBoost;
import cz.neumimto.rpg.effects.common.stacking.ValueProcessor;
import cz.neumimto.rpg.persistance.GroupDao;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.SkillTree;
import javassist.CannotCompileException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class Tests {

    // @Test
    public void testConfig() throws NoSuchFieldException, IllegalAccessException {
        ResourceLoader.raceDir = new File("./src/main/test/testfiles/races");
        ResourceLoader.guildsDir = new File("./src/main/test/testfiles/guilds");
        ResourceLoader.classDir = new File("./src/main/test/testfiles/classes");
        GroupDao dao = new GroupDao();
        dao.loadGuilds();
        dao.loadRaces();

        SkillService sk = mock(SkillService.class);
        when(sk.getSkillTrees()).thenReturn(new HashMap<String, SkillTree>() {
            {
                put("test", SkillTree.Default);
            }
        });
        Field f = dao.getClass().getDeclaredField("skillService");
        f.setAccessible(true);
        f.set(dao, sk);
        dao.loadNClasses();
        Assert.assertTrue(ResourceLoader.raceDir.listFiles().length == dao.getRaces().size());
        Assert.assertTrue(ResourceLoader.guildsDir.listFiles().length == dao.getGuilds().size());
        Assert.assertTrue(dao.getClasses().get("test").getLevels().length == 99);
    }


   // @Test
    public void testEffectClassGenerator() {
        ClassGenerator classGenerator = new ClassGenerator();
        IGlobalEffect eff = null;
        try {
            eff = classGenerator.generateGlobalEffect(SpeedBoost.class);
            Assert.assertTrue(eff != null);
            classGenerator.injectGlobalEffectField(SpeedBoost.class, eff);
        } catch (CannotCompileException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(SpeedBoost.global == eff);
    }

    @Test
    public void testDynamicListener() throws Exception {
        Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
        ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        ClassGenerator classGenerator = new ClassGenerator();
        try (InputStreamReader rs = new InputStreamReader(new FileInputStream(new File("./src/main/test/testfiles/js/eventgen/test.js")))) {
            engine.eval(rs);
            HashMap map = (HashMap) engine.get("events");
            Object o = classGenerator.generateDynamicListener(map);
            DamageEntityEvent mock = mock(DamageEntityEvent.class);
            o.getClass().getMethod("onDamageEntityEvent", DamageEntityEvent.class).invoke(o, mock);
            MoveEntityEvent mock2 = mock(MoveEntityEvent.class);
            o.getClass().getMethod("onDisplaceEntityEvent", MoveEntityEvent.class).invoke(o, mock2);
        } catch (ScriptException | IOException e) {
            e.printStackTrace();
        }
    }

}
