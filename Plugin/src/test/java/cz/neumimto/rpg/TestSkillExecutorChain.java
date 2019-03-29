package cz.neumimto.rpg;

import cz.neumimto.rpg.configuration.AttributeConfiguration;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillSettings;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TestSkillExecutorChain {

    ActiveCharacter activeCharacter;

    private SkillData skillData;

    private static Set<String> complexKeySuffixes;

    private static Set<Attribute> attributes;

    static Attribute str;

    static Attribute agi;

    @BeforeClass
    public static void init() throws Exception {
        TestHelper.initLocalizations();
        TestHelper.setupLog();

        complexKeySuffixes = new HashSet<>();
        complexKeySuffixes.add(SkillSettings.bonus);
        complexKeySuffixes.add("_per_ntrpg:strength");
        complexKeySuffixes.add("_per_ntrpg:agility");

        attributes = new HashSet<>();
        AttributeConfiguration attributeConfiguration = new AttributeConfiguration();
        TestUtils.setField(attributeConfiguration, "id", "ntrpg:agility");
        str = new Attribute(attributeConfiguration);

        attributes.add(str);
        TestUtils.setField(attributeConfiguration, "id", "ntrpg:strength");
        agi = new Attribute(attributeConfiguration);

        attributes.add(agi);

    }

    @Before
    public void before() {
        skillData = new SkillData("test");
        SkillSettings skillSettings = new SkillSettings();
        skillSettings.addNode("damage", 100, 15);
        skillSettings.addNode("range", 100, 10);
        skillSettings.addNode("manacost", 100, -1);
        skillData.setSkillSettings(skillSettings);


        CharacterBase characterBase = new CharacterBase();
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put(str.getId(), 0);
        objectObjectHashMap.put(agi.getId(), 0);
        TestUtils.setField(characterBase, "cachedAttributes", objectObjectHashMap);

        activeCharacter = new ActiveCharacter(UUID.randomUUID(), characterBase);


    }


    @Test
    public void test_skill_settings_cache_leveled() {
        PlayerSkillContext context = new PlayerSkillContext(null, null, activeCharacter);
        context.setSkillData(skillData);
        TestUtils.setField(context, "cachedComputedSkillSettings", new Object2FloatOpenHashMap());
        context.setLevel(3);
        context.populateCache(complexKeySuffixes, attributes);



        AbstractObject2FloatMap<String> cachedComputedSkillSettings = context.getCachedComputedSkillSettings();
        Assert.assertNotNull(cachedComputedSkillSettings);

        for (String s : cachedComputedSkillSettings.keySet()) {
            Assert.assertTrue(!s.endsWith(SkillSettings.bonus));
        }

        for (String s : cachedComputedSkillSettings.keySet()) {
            for (Attribute a : attributes) {
                Assert.assertTrue(!s.contains(a.getId()));
            }
        }

        Assert.assertSame(3, cachedComputedSkillSettings.size());
        Assert.assertTrue(100.0f + 15 * 3 == cachedComputedSkillSettings.getFloat("damage"));
        Assert.assertTrue(100.0f + 10 * 3 == cachedComputedSkillSettings.getFloat("range"));
        Assert.assertTrue(100.0f + -1 * 3 == cachedComputedSkillSettings.getFloat("manacost"));

    }

    @Test
    public void test_skill_settings_cache_attribute() {


        PlayerSkillContext context = new PlayerSkillContext(null, null, activeCharacter);
        context.setSkillData(skillData);
        TestUtils.setField(context, "cachedComputedSkillSettings", new Object2FloatOpenHashMap());
        context.setLevel(0);
        skillData.getSkillSettings().addNode("damage_per_"+agi.getId(),10,0);
        skillData.getSkillSettings().addNode("range_per_"+str.getId(),10,0);
        activeCharacter.getTransientAttributes().put(str.getId(), 3);
        activeCharacter.getTransientAttributes().put(agi.getId(), 2);

        context.populateCache(complexKeySuffixes, attributes);



        AbstractObject2FloatMap<String> cachedComputedSkillSettings = context.getCachedComputedSkillSettings();
        Assert.assertNotNull(cachedComputedSkillSettings);

        for (String s : cachedComputedSkillSettings.keySet()) {
            Assert.assertTrue(!s.endsWith(SkillSettings.bonus));
        }

        for (String s : cachedComputedSkillSettings.keySet()) {
            for (Attribute a : attributes) {
                Assert.assertTrue(!s.contains(a.getId()));
            }
        }

        Assert.assertSame(3, cachedComputedSkillSettings.size());
        Assert.assertTrue(100.0f + 10 * 2 == cachedComputedSkillSettings.getFloat("damage"));
        Assert.assertTrue(100.0f + 10 * 3 == cachedComputedSkillSettings.getFloat("range"));
        Assert.assertTrue(100.0f == cachedComputedSkillSettings.getFloat("manacost"));

    }

}
