package cz.neumimto.rpg;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.SkillSettings;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestDictionary;
import cz.neumimto.rpg.junit.TestGuiceModule;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ExtendWith({NtRpgExtension.class, GuiceExtension.class})
@IncludeModule(TestGuiceModule.class)
public class TestSkillExecutorChain {

    ActiveCharacter activeCharacter;

    private SkillData skillData;

    private static Set<AttributeConfig> attributes;

    static AttributeConfig str;

    @Inject
    private PropertyService propertyService;

    @BeforeAll
    public static void init() throws Exception {
        TestHelper.setupLog();

        str = TestDictionary.STR;

        attributes = new HashSet<>();
        attributes.add(str);


    }

    @BeforeEach
    public void before() {
        skillData = new SkillData("test");
        SkillSettings skillSettings = new SkillSettings();
        skillSettings.addExpression("damage", 100);
        skillSettings.addExpression("range", "100  + 10 * str");
        skillSettings.addExpression("manacost", "100  - 1 * level");
        skillData.setSkillSettings(skillSettings);


        CharacterBase characterBase = new CharacterBase();
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put(str.getId(), 3);

        activeCharacter = new TestCharacter(UUID.randomUUID(), characterBase, 0);
        activeCharacter.getTransientAttributes().put(str.getId(), 3);
        propertyService.getAttributes().put("str", str);
    }


    @Test
    public void test_skill_settings_cache_leveled() {
        PlayerSkillContext context = new PlayerSkillContext(null, null, activeCharacter) {
            @Override
            protected Set<String> getAttributeIds() {
                return propertyService.getAttributes().keySet();
            }
        };
        context.setSkillData(skillData);
        context.setLevel(3);

        Object2DoubleOpenHashMap<String> cachedComputedSkillSettings = context.getCachedComputedSkillSettings();
        Assertions.assertNotNull(cachedComputedSkillSettings);

        Assertions.assertSame(3, cachedComputedSkillSettings.size());
        Assertions.assertTrue(100.0f == cachedComputedSkillSettings.getDouble("damage"));
        Assertions.assertTrue(100.0f + 10 * 3 == cachedComputedSkillSettings.getDouble("range"));
        Assertions.assertTrue(100.0f + -1 * 3 == cachedComputedSkillSettings.getDouble("manacost"));

    }

}
