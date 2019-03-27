package cz.neumimto.rpg;

import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillSettings;
import org.junit.Before;
import org.junit.BeforeClass;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TestSkillExecutorChain {

    ActiveCharacter activeCharacter;

    private SkillData skillData;

    private static Set<String> complexKeySuffixes;

    private static Set<Attribute> attributes;

    @BeforeClass
    public static void init() throws Exception {
        TestHelper.initLocalizations();

        complexKeySuffixes = new HashSet<>();
        complexKeySuffixes.add(SkillSettings.bonus);
        complexKeySuffixes.add("_per_ntrpg:strength");
        complexKeySuffixes.add("_per_ntrpg:agility");

        attributes = new HashSet<>();
        Attribute attribute = new Attribute();

        attributes.add()
    }

    @Before
    public void before() {
        skillData = new SkillData("test");
        SkillSettings skillSettings = new SkillSettings();
        skillSettings.addNode("damage", 10, 15);
        skillSettings.addNode("range", 100, 10);
        skillSettings.addNode("manacost", 100, -1);
        skillData.setSkillSettings(skillSettings);

        activeCharacter = new ActiveCharacter(UUID.randomUUID(), new CharacterBase());


    }


    public void test_skill_settings_cache() {

        PlayerSkillContext context = new PlayerSkillContext(null, null, activeCharacter);
        context.setSkillData(skillData);


        context.populateCache(complexKeySuffixes, );
    }

}
