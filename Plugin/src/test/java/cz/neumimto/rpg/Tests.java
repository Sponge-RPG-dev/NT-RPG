package cz.neumimto.rpg;


//@RunWith(PowerMockRunner.class)
public class Tests {
/*
	@Test
	public void testDynamicListener() throws Exception {
		Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
		ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
		ClassGenerator classGenerator = new ClassGenerator();
		try (InputStreamReader rs = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("js/eventgen/test.js"))) {
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
	public void k() {
		int x = -256;
		boolean is16 = (x & 0x0F) == 0;
		Assert.assertTrue(is16);
		is16 = x == (x >> 4) << 4;
		Assert.assertTrue(is16);
	}

	private static Logger logger;

        @Test
        public void testPermissionsManagement() {
            ClassService classService = new ClassService();
            ActiveCharacter character = mock(ActiveCharacter.class);
            when(character.getLevel()).thenReturn(2);
            ClassDefinition race = new ClassDefinition("a");
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

            PlayerClassData nClass = new PlayerClassData(character);

            ClassDefinition c = new ClassDefinition("b");


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
            when(character.getClasses()).thenReturn(new HashSet<>(Arrays.asList(nClass)));
            ClassDefinition changeTo = new ClassDefinition("c");
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
		logger = LoggerFactory.getLogger(Tests.class);
		Log.logger = logger;
	}

	*/
}




