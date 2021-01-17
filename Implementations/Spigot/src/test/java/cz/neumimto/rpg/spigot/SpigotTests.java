package cz.neumimto.rpg.spigot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scheduler.BukkitSchedulerMock;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.entity.players.PreloadCharacter;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.concurrent.*;

public class SpigotTests {

    static ServerMock server;
    static SpigotRpgPlugin plugin;

    static ClassService classService;
    static CharacterService<ISpigotCharacter> characterService;
    static PluginConfig pluginConfig;
    static Executor executor;

    @BeforeAll
    public static void setUp()
    {
        server = MockBukkit.mock();
        plugin = (SpigotRpgPlugin) MockBukkit.load(SpigotRpgPlugin.class);
        classService = Rpg.get().getClassService();
        characterService = Rpg.get().getCharacterService();
        executor = Executors.newSingleThreadExecutor();
        pluginConfig = Rpg.get().getPluginConfig();
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
        PlayerMock tester = preparePlayer();
        Assertions.assertFalse(characterService.getCharacter(tester.getUniqueId()) instanceof PreloadCharacter);
    }

    private PlayerMock preparePlayer() {
        PlayerMock tester = server.addPlayer("Tester");

        Wait.mainThread(500);
        BukkitSchedulerMock scheduler = (BukkitSchedulerMock) Bukkit.getScheduler();
        scheduler.performTicks(10);
        return tester;
    }

    @Test
    public void player_test_common_stuff() {

        PlayerMock playerMock = preparePlayer();
        playerMock.addAttachment(plugin, "ntrpg.class.warrior", true);

        // select primary class
        playerMock.performCommand("char choose class warrior");
        Wait.mainThread(500);
        ISpigotCharacter character = characterService.getCharacter(playerMock.getUniqueId());
        Assertions.assertTrue(character.getClasses().containsKey("warrior"));

        // add level
        ClassDefinition warriorClass = classService.getClassDefinitionByName("warrior");
        double levelTreshold = warriorClass.getLevelProgression().getLevelMargins()[0] + 1D;
        PlayerClassData playerClass = character.getClassByName("warrior");
        characterService.addExperiences(character, levelTreshold, playerClass);
        Assertions.assertSame(1, playerClass.getLevel(), "First Level");

        // check level changes
        CharacterClass characterClass = playerClass.getCharacterClass();
        Assertions.assertSame(characterClass.getSkillPoints(), warriorClass.getSkillpointsPerLevel() , "Player receive a skill-point");
        Assertions.assertSame(character.getAttributePoints(),
                warriorClass.getAttributepointsPerLevel() + pluginConfig.ATTRIBUTEPOINTS_ON_START, "Player receive an attribute-point");

        // learn skill
        playerMock.performCommand("skill Punch LEARN warrior");
        Wait.mainThread(300);
        Assertions.assertTrue(character.hasSkill("Punch"), "Player has skill Punch test");
        PlayerSkillContext punchCtx = character.getSkill("Punch");

        // test skill before upgrade
        float knockback = punchCtx.getCachedComputedSkillSettings().getFloat("knockback");
        Assertions.assertEquals(0f, knockback, "knockback = 0 test");

        // add level
        levelTreshold = warriorClass.getLevelProgression().getLevelMargins()[1] + 1D;
        characterService.addExperiences(character, levelTreshold, playerClass);
        Assertions.assertSame(2, playerClass.getLevel(), "Second Level");

        // learn skill upgrade
        playerMock.performCommand("skill Punch_Knockback_Upgrade LEARN warrior");
        Wait.mainThread(300);
        Assertions.assertTrue(character.hasSkill("Punch_Knockback_Upgrade"), "Player has skill Bash_Upgrade");

        // test skill after upgrade
        Assertions.assertTrue(knockback < punchCtx.getCachedComputedSkillSettings().getFloat("knockback"), "Skill upgraded test");

    }

    @AfterAll
    public static void tearDown()
    {
       // MockBukkit.unmock();
    }
}
