package cz.neumimto.rpg;

import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.junit.TestGuiceModule;
import cz.neumimto.rpg.model.CharacterBaseTest;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ExtendWith({NtRpgExtension.class, GuiceExtension.class})
@IncludeModule(TestGuiceModule.class)
public class TestSkillExecutorChain {

    ActiveCharacter activeCharacter;

    private SkillData skillData;

    private static Set<String> complexKeySuffixes;

    private static Set<AttributeConfig> attributes;

    static AttributeConfig str;

    static AttributeConfig agi;

    @BeforeAll
    public static void init() throws Exception {
        TestHelper.setupLog();

        complexKeySuffixes = new HashSet<>();
        complexKeySuffixes.add(SkillSettings.BONUS_SUFFIX);


        str = TestDictionary.STR;
        agi = TestDictionary.AGI;

        complexKeySuffixes.add("_per_"+str.getId());
        complexKeySuffixes.add("_per_"+agi.getId());

        attributes = new HashSet<>();
        attributes.add(str);
        attributes.add(agi);

    }

    @BeforeEach
    public void before() {
        skillData = new SkillData("test");
        SkillSettings skillSettings = new SkillSettings();
        skillSettings.addNode("damage", 100, 15);
        skillSettings.addNode("range", 100, 10);
        skillSettings.addNode("manacost", 100, -1);
        skillData.setSkillSettings(skillSettings);


        CharacterBase characterBase = new CharacterBaseTest();
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put(str.getId(), 0);
        objectObjectHashMap.put(agi.getId(), 0);
        TestUtils.setField(characterBase, "cachedAttributes", objectObjectHashMap);

        activeCharacter = new TestCharacter(UUID.randomUUID(), characterBase, 0);


    }


    @Test
    public void test_skill_settings_cache_leveled() {
        PlayerSkillContext context = new PlayerSkillContext(null, null, activeCharacter);
        context.setSkillData(skillData);
        TestUtils.setField(context, "cachedComputedSkillSettings", new Object2FloatOpenHashMap());
        context.setLevel(3);
        context.populateCache(complexKeySuffixes, attributes);



        AbstractObject2FloatMap<String> cachedComputedSkillSettings = context.getCachedComputedSkillSettings();
        Assertions.assertNotNull(cachedComputedSkillSettings);

        for (String s : cachedComputedSkillSettings.keySet()) {
            Assertions.assertTrue(!s.endsWith(SkillSettings.BONUS_SUFFIX));
        }

        for (String s : cachedComputedSkillSettings.keySet()) {
            for (AttributeConfig a : attributes) {
                Assertions.assertTrue(!s.contains(a.getId()));
            }
        }

        Assertions.assertSame(3, cachedComputedSkillSettings.size());
        Assertions.assertTrue(100.0f + 15 * 3 == cachedComputedSkillSettings.getFloat("damage"));
        Assertions.assertTrue(100.0f + 10 * 3 == cachedComputedSkillSettings.getFloat("range"));
        Assertions.assertTrue(100.0f + -1 * 3 == cachedComputedSkillSettings.getFloat("manacost"));

    }

    @Test
    public void test_skill_settings_cache_attribute() {


        PlayerSkillContext context = new PlayerSkillContext(null, null, activeCharacter);
        context.setSkillData(skillData);
        TestUtils.setField(context, "cachedComputedSkillSettings", new Object2FloatOpenHashMap());
        context.setLevel(0);

        skillData.getSkillSettings().addAttributeNode("damage", agi,10);
        skillData.getSkillSettings().addAttributeNode("range", str,10);

        activeCharacter.getTransientAttributes().put(str.getId(), 3);
        activeCharacter.getTransientAttributes().put(agi.getId(), 2);

        context.populateCache(complexKeySuffixes, attributes);



        AbstractObject2FloatMap<String> cachedComputedSkillSettings = context.getCachedComputedSkillSettings();
        Assertions.assertNotNull(cachedComputedSkillSettings);

        for (String s : cachedComputedSkillSettings.keySet()) {
            Assertions.assertTrue(!s.endsWith(SkillSettings.BONUS_SUFFIX));
        }

        for (String s : cachedComputedSkillSettings.keySet()) {
            for (AttributeConfig a : attributes) {
                Assertions.assertTrue(!s.contains(a.getId()));
            }
        }

        Assertions.assertSame(3, cachedComputedSkillSettings.size());
        Assertions.assertTrue(100.0f + 10 * 2 == cachedComputedSkillSettings.getFloat("damage"));
        Assertions.assertTrue(100.0f + 10 * 3 == cachedComputedSkillSettings.getFloat("range"));
        Assertions.assertTrue(100.0f == cachedComputedSkillSettings.getFloat("manacost"));

    }

}
