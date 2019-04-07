package cz.neumimto.rpg;


import cz.neumimto.rpg.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.players.*;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class ClassManipulationTests {

    ClassDefinition pc1;
    ClassDefinition pc2;
    ClassDefinition pc3;


    ClassDefinition ps1;
    ClassDefinition ps2;
    ClassDefinition ps3;

    CharacterService characterService = new CharacterService() {
        @Override
        protected void addCharacterToGame(UUID id, IActiveCharacter character, List<CharacterBase> playerChars) {

        }

        @Override
        public void updateWeaponRestrictions(IActiveCharacter character) {

        }

        @Override
        public void updateArmorRestrictions(IActiveCharacter character) {

        }
    };

    ActiveCharacter character;

    @BeforeEach
    public static void init() throws Exception {
        TestHelper.initLocalizations();
    }

    @BeforeAll
    public void before() throws Exception {
        //lets not invoke constructor
        PluginConfig o = (PluginConfig) TestHelper.getUnsafe().allocateInstance(PluginConfig.class);
        o.PRIMARY_CLASS_TYPE = "Primary";
        NtRpgPlugin.pluginConfig = o;

        NtRpgPlugin.pluginConfig.CLASS_TYPES = new LinkedHashMap<String, ClassTypeDefinition>() {{
            put("Primary", new ClassTypeDefinition(null, null, null, false, 1));
            put("Secondary", new ClassTypeDefinition(null, null, null, false, 2));
        }};

        pc1 = new ClassDefinition("class1", "Primary");
        pc2 = new ClassDefinition("class2", "Primary");
        pc3 = new ClassDefinition("class3", "Primary");

        pc1.getClassDependencyGraph().getConflicts().addAll(Arrays.asList(pc2, pc3));
        pc2.getClassDependencyGraph().getConflicts().addAll(Arrays.asList(pc1, pc3));
        pc3.getClassDependencyGraph().getConflicts().addAll(Arrays.asList(pc1, pc2));

        ps1 = new ClassDefinition("secondary1", "Secondary");
        ps2 = new ClassDefinition("secondary2", "Secondary");
        ps3 = new ClassDefinition("secondary3", "Secondary");

        ps1.getClassDependencyGraph().getConflicts().addAll(Arrays.asList(ps2, ps3));
        ps2.getClassDependencyGraph().getConflicts().addAll(Arrays.asList(ps1, ps3));
        ps3.getClassDependencyGraph().getConflicts().addAll(Arrays.asList(ps1, ps2));

        CharacterBase base = new CharacterBase();
        character = new ActiveCharacter(UUID.randomUUID(), base);
    }


    @Test
    public void may_assign_primary_primary_as_first() {
        ActionResult result = characterService.canGainClass(character, pc1);
        Assertions.assertTrue(result.isOk());
    }

    @Test
    public void may_not_select_same_type() {
        CharacterClass characterClass = new CharacterClass();
        PlayerClassData playerClassData = new PlayerClassData(pc1, characterClass);
        character.addClass(playerClassData);
        ActionResult result = characterService.canGainClass(character, pc2);
        Assertions.assertTrue(!result.isOk());
    }

    @Test
    public void respects_class_selection_order() {
        NtRpgPlugin.pluginConfig.RESPECT_CLASS_SELECTION_ORDER = true;
        ActionResult result = characterService.canGainClass(character, ps2);
        Assertions.assertTrue(!result.isOk());
    }

    @Test
    public void select_secondary_class() {
        NtRpgPlugin.pluginConfig.RESPECT_CLASS_SELECTION_ORDER = true;
        CharacterClass characterClass = new CharacterClass();
        PlayerClassData playerClassData = new PlayerClassData(pc1, characterClass);
        character.addClass(playerClassData);

        ActionResult result = characterService.canGainClass(character, ps1);
        Assertions.assertTrue(result.isOk());
    }

    @Test
    public void select_secondary_before_primary() {
        NtRpgPlugin.pluginConfig.RESPECT_CLASS_SELECTION_ORDER = false;
        ActionResult result = characterService.canGainClass(character, ps2);
        Assertions.assertTrue(result.isOk());
    }

}
