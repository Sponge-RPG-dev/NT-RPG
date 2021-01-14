package cz.neumimto.rpg.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.entity.players.PreloadCharacter;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.concurrent.*;

public class SpigotTests {

    static ServerMock server;
    static SpigotRpgPlugin plugin;

    static ClassService classService;
    static CharacterService<ISpigotCharacter> characterService;

    static Executor executor;

    @BeforeAll
    public static void setUp()
    {
        server = MockBukkit.mock();
        plugin = (SpigotRpgPlugin) MockBukkit.load(SpigotRpgPlugin.class);
        classService = Rpg.get().getClassService();
        characterService = Rpg.get().getCharacterService();
        executor = Executors.newSingleThreadExecutor();
    }

    @Test
    public void check_default_classes() {
        Map<String, ClassDefinition> classes = classService.getClasses();
        ClassDefinition apprentice = classes.get("apprentice");
        Assertions.assertNotNull(apprentice);
        Assertions.assertFalse(apprentice.getSkillTree().getSkills().isEmpty());

        ClassDefinition warrior = classes.get("warrior");
        Assertions.assertNotNull(warrior);
        Assertions.assertFalse(warrior.getSkillTree().getSkills().isEmpty());
    }

    @Test
    public void check_skill_upgrades() {
        ClassDefinition warrior = classService.getClassDefinitionByName("warrior");
        SkillTree skillTree = warrior.getSkillTree();
        SkillData bUpg = skillTree.getSkillById("ntrpg:bash_upg_i");
        Assertions.assertNotNull(bUpg);

        SkillData bash = skillTree.getSkillById("ntrpg:bash");
        Assertions.assertNotNull(bash);

        Assertions.assertTrue(bash.getUpgradedBy().contains(bUpg));
        Assertions.assertTrue(bUpg.getUpgrades().containsKey(bash.getSkillId()));
    }

    @Test
    public void player_login_char_autocreated_test() {
        PlayerMock tester = server.addPlayer("Tester");

        CountDownLatch lock = new CountDownLatch(1);
        try {
            lock.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BukkitSchedulerMock scheduler = (BukkitSchedulerMock) Bukkit.getScheduler();
        scheduler.performTicks(10);
        Assertions.assertFalse(characterService.getCharacter(tester.getUniqueId()) instanceof PreloadCharacter);
        int i = 0;
    }

    @AfterAll
    public static void tearDown()
    {
       // MockBukkit.unmock();
    }
}
