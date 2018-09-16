package cz.neumimto.rpg;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.players.groups.PlayerGroupPermission;
import cz.neumimto.rpg.players.groups.Race;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

@RunWith(PowerMockRunner.class)
public class Tests {

	private static Logger logger;

	@BeforeClass
	public static void setupLogger() {
		logger = LoggerFactory.getLogger(Tests.class);
		Log.logger = logger;
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
			o.getClass().getMethod("onMoveEntityEvent", MoveEntityEvent.class).invoke(o, mock2);
		} catch (ScriptException | IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPermissionsManagement() {
		GroupService groupService = new GroupService();
		ActiveCharacter character = mock(ActiveCharacter.class);
		when(character.getLevel()).thenReturn(2);
		Race race = new Race("a");
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
		when(character.getRace()).thenReturn(race);

		ExtendedNClass nClass = new ExtendedNClass(character);

		ConfigClass c = new ConfigClass("b");

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

		nClass.setConfigClass(c);

		when(character.getPrimaryClass()).thenReturn(nClass);
		when(character.getClasses()).thenReturn(new HashSet<ExtendedNClass>(Arrays.asList(nClass)));
		ConfigClass changeTo = new ConfigClass("c");
		changeTo.setPermissions(new HashSet<PlayerGroupPermission>() {{
			add(new PlayerGroupPermission() {{
				setLevel(1);
				setPermissions(new HashSet<>(Arrays.asList("class1", "class4", "common2")));
			}});
		}});

		Set<String> permissionsToRemove = groupService.getPermissionsToRemove(character, nClass.getConfigClass());
		Assert.assertFalse(permissionsToRemove.contains("common2"));
		Assert.assertFalse(permissionsToRemove.contains("common1"));
		Assert.assertTrue(permissionsToRemove.contains("class1"));
		Assert.assertTrue(permissionsToRemove.contains("class2"));
		Assert.assertTrue(permissionsToRemove.contains("class4"));
	}

	@Test
	public void k() {
		int x = -256;
		boolean is16 = (x & 0x0F) == 0;
		Assert.assertTrue(is16);
		is16 = x == (x >> 4) << 4;
		Assert.assertTrue(is16);
	}
}




