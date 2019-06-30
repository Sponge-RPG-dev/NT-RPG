package cz.neumimto.rpg;


import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.classes.ClassServiceImpl;
import cz.neumimto.rpg.common.persistance.model.JPACharacterClass;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassPermission;
import cz.neumimto.rpg.sponge.scripting.SpongeClassGenerator;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static org.mockito.Mockito.when;

public class Tests {

    @Test
    public void testDynamicListener() throws Exception {
        Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
        ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        ClassGenerator classGenerator = new SpongeClassGenerator();
        try (InputStreamReader rs = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("js/eventgen/test.js"))) {
            engine.eval(rs);
            List list = (List) engine.get("events");
            Object o = classGenerator.generateDynamicListener(list);
            Assertions.assertSame(o.getClass().getDeclaredMethods().length, 3);
        } catch (ScriptException | IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void k() {
        int x = -256;
        boolean is16 = (x & 0x0F) == 0;
        Assertions.assertTrue(is16);
        is16 = x == (x >> 4) << 4;
        Assertions.assertTrue(is16);
    }

    @Test
    public void testPermissionsManagement() {
        ClassService classService = new ClassServiceImpl();
        ActiveCharacter character = Mockito.mock(ActiveCharacter.class);
        when(character.getLevel()).thenReturn(2);

        ClassDefinition race = new ClassDefinition("a", "Primary");
        race.setPermissions(new HashSet<PlayerClassPermission>() {{
            add(new PlayerClassPermission() {{
                setLevel(1);
                setPermissions(Arrays.asList("race1", "race2", "common2"));
            }});
            add(new PlayerClassPermission() {{
                setLevel(2);
                setPermissions(Arrays.asList("race3", "common1"));
            }});
        }});

        CharacterClass characterClass = new JPACharacterClass();
        characterClass.setLevel(2);

        PlayerClassData nClass = new PlayerClassData(race, characterClass);

        ClassDefinition c = new ClassDefinition("b", "Primary");

        c.setPermissions(new HashSet<PlayerClassPermission>() {
            {
                add(new PlayerClassPermission() {{
                    setLevel(1);
                    setPermissions(Arrays.asList("class1", "class2", "common2"));
                }});
                add(new PlayerClassPermission() {{
                    setLevel(2);
                    setPermissions(Arrays.asList("class4", "common1"));
                }});
            }
        });

        nClass.setClassDefinition(c);

        when(character.getPrimaryClass()).thenReturn(nClass);
        Map<String, PlayerClassData> map = new HashMap<>();
        map.put(race.getName(), new PlayerClassData(race, characterClass));


        CharacterClass characterClass2 = new JPACharacterClass();
        characterClass.setLevel(2);

        map.put(c.getName(), new PlayerClassData(c, characterClass2));

        when(character.getClasses()).thenReturn(map);

        ClassDefinition changeTo = new ClassDefinition("c", "Primary");
        changeTo.setPermissions(new HashSet<PlayerClassPermission>() {{
            add(new PlayerClassPermission() {{
                setLevel(1);
                setPermissions(Arrays.asList("class1", "class4", "common2"));
            }});
        }});

        Set<String> permissionsToRemove = classService.getPermissionsToRemove(character, nClass.getClassDefinition());

        Assertions.assertFalse(permissionsToRemove.contains("common2"));
        Assertions.assertFalse(permissionsToRemove.contains("common1"));
        Assertions.assertTrue(permissionsToRemove.contains("class1"));
        Assertions.assertTrue(permissionsToRemove.contains("class2"));
        Assertions.assertTrue(permissionsToRemove.contains("class4"));
    }


    @BeforeAll
    public static void setupLogger() {
        Log.setLogger(LoggerFactory.getLogger(Tests.class));
    }


}




