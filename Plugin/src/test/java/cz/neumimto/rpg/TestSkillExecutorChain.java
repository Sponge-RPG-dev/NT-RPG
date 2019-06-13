package cz.neumimto.rpg;

import cz.neumimto.rpg.common.persistance.model.JPACharacterBase;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.entity.players.attributes.AttributeConfig;
import cz.neumimto.rpg.sponge.configuration.AttributeConfiguration;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacter;
import it.unimi.dsi.fastutil.objects.AbstractObject2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        complexKeySuffixes.add(SkillSettings.bonus);
        complexKeySuffixes.add("_per_ntrpg:strength");
        complexKeySuffixes.add("_per_ntrpg:agility");

        attributes = new HashSet<>();
        AttributeConfiguration attributeConfiguration = new AttributeConfiguration();
        TestUtils.setField(attributeConfiguration, "id", "ntrpg:agility");
        str = new AttributeConfig(attributeConfiguration);

        attributes.add(str);
        TestUtils.setField(attributeConfiguration, "id", "ntrpg:strength");
        agi = new AttributeConfig(attributeConfiguration);

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


        JPACharacterBase characterBase = new JPACharacterBase();
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put(str.getId(), 0);
        objectObjectHashMap.put(agi.getId(), 0);
        TestUtils.setField(characterBase, "cachedAttributes", objectObjectHashMap);

        activeCharacter = new SpongeCharacter(UUID.randomUUID(), characterBase, 0);


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
            Assertions.assertTrue(!s.endsWith(SkillSettings.bonus));
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
            Assertions.assertTrue(!s.endsWith(SkillSettings.bonus));
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
