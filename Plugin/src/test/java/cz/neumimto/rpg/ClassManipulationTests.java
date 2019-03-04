package cz.neumimto.rpg;


import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

public class ClassManipulationTests {

    ClassDefinition pc1;
    ClassDefinition pc2;
    ClassDefinition pc3;

    CharacterService characterService = new CharacterService();

    ActiveCharacter character;

    @BeforeClass
    public static void init() throws Exception {
        TestHelper.initLocalizations();
    }

    @Before
    public void before() throws Exception {
        //lets not invoke constructor
        PluginConfig o = (PluginConfig) TestHelper.getUnsafe().allocateInstance(PluginConfig.class);
        o.PRIMARY_CLASS_TYPE = "Primary";
        NtRpgPlugin.pluginConfig = o;

        pc1 = new ClassDefinition("class1", "Primary");
        pc2 = new ClassDefinition("class2", "Primary");
        pc3 = new ClassDefinition("class3", "Primary");

        pc1 = new ClassDefinition("secondary1", "Secondary");
        pc2 = new ClassDefinition("secondary2", "Secondary");
        pc3 = new ClassDefinition("secondary3", "Secondary");

        CharacterBase base = new CharacterBase();
        character = new ActiveCharacter(UUID.randomUUID(), base);
    }


    @Test
    public void may_assign_primary_primary_as_first() {
        characterService.canGainClass(character, pc1);
    }
}
