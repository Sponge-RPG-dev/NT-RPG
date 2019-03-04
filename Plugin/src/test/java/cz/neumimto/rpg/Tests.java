package cz.neumimto.rpg;


import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.players.groups.PlayerGroupPermission;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class Tests {

    @Test
    public void testDynamicListener() throws Exception {
        Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
        ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        ClassGenerator classGenerator = new ClassGenerator();
        try (InputStreamReader rs = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("js/eventgen/test.js"))) {
            engine.eval(rs);
            HashMap map = (HashMap) engine.get("events");
            Object o = classGenerator.generateDynamicListener(map);
            DamageEntityEvent damageEntityEvent = Mockito.mock(DamageEntityEvent.class);
            o.getClass().getMethod("onDamageEntityEvent", DamageEntityEvent.class).invoke(o, damageEntityEvent);
            MoveEntityEvent moveEntityEvent = Mockito.mock(MoveEntityEvent.class);
            o.getClass().getMethod("onMoveEntityEvent", MoveEntityEvent.class).invoke(o, moveEntityEvent);
        } catch (ScriptException | IOException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(true);
    }

    @Test
    public void k() {
        int x = -256;
        boolean is16 = (x & 0x0F) == 0;
        Assert.assertTrue(is16);
        is16 = x == (x >> 4) << 4;
        Assert.assertTrue(is16);
    }

    @Test
    public void testPermissionsManagement() {
        ClassService classService = new ClassService();
        ActiveCharacter character = Mockito.mock(ActiveCharacter.class);
        when(character.getLevel()).thenReturn(2);

        ClassDefinition race = new ClassDefinition("a", "Primary");
        race.setPermissions(new HashSet<PlayerGroupPermission>() {{
            add(new PlayerGroupPermission() {{
                setLevel(1);
                setPermissions(new HashSet<>(Arrays.asList("race1", "race2", "common2")));
            }});
            add(new PlayerGroupPermission() {{
                setLevel(2);
                setPermissions(new HashSet<>(Arrays.asList("race3", "common1")));
            }});
        }});

        CharacterClass characterClass = new CharacterClass();
        characterClass.setLevel(2);

        PlayerClassData nClass = new PlayerClassData(race, characterClass);

        ClassDefinition c = new ClassDefinition("b", "Primary");

        c.setPermissions(new HashSet<PlayerGroupPermission>() {
            {
                add(new PlayerGroupPermission() {{
                    setLevel(1);
                    setPermissions(new HashSet<>(Arrays.asList("class1", "class2", "common2")));
                }});
                add(new PlayerGroupPermission() {{
                    setLevel(2);
                    setPermissions(new HashSet<>(Arrays.asList("class4", "common1")));
                }});
            }
        });

        nClass.setClassDefinition(c);

        when(character.getPrimaryClass()).thenReturn(nClass);
        Map<String, PlayerClassData> map = new HashMap<>();
        map.put(race.getName(), new PlayerClassData(race, characterClass));


        CharacterClass characterClass2 = new CharacterClass();
        characterClass.setLevel(2);

        map.put(c.getName(), new PlayerClassData(c, characterClass2));

        when(character.getClasses()).thenReturn(map);

        ClassDefinition changeTo = new ClassDefinition("c", "Primary");
        changeTo.setPermissions(new HashSet<PlayerGroupPermission>() {{
            add(new PlayerGroupPermission() {{
                setLevel(1);
                setPermissions(new HashSet<>(Arrays.asList("class1", "class4", "common2")));
            }});
        }});

        Set<String> permissionsToRemove = classService.getPermissionsToRemove(character, nClass.getClassDefinition());

        Assert.assertFalse(permissionsToRemove.contains("common2"));
        Assert.assertFalse(permissionsToRemove.contains("common1"));
        Assert.assertTrue(permissionsToRemove.contains("class1"));
        Assert.assertTrue(permissionsToRemove.contains("class2"));
        Assert.assertTrue(permissionsToRemove.contains("class4"));
    }


    @BeforeClass
    public static void setupLogger() {
        Log.logger = LoggerFactory.getLogger(Tests.class);
    }


}




