package cz.neumimto.persistence;

import cz.neumimto.rpg.common.model.*;
import cz.neumimto.rpg.common.persistance.model.BaseCharacterAttributeImpl;
import cz.neumimto.rpg.common.persistance.model.CharacterBaseImpl;
import cz.neumimto.rpg.common.persistance.model.CharacterClassImpl;
import cz.neumimto.rpg.common.persistance.model.CharacterSkillImpl;

import java.util.*;

public class TestHelper {

    public static CharacterBaseImpl createCharacterBase() {
        CharacterBaseImpl characterBase = new CharacterBaseImpl();
        characterBase.setId(null);
        characterBase.setAttributePoints(1);
        characterBase.setAttributePointsSpent(2);
        characterBase.setCanResetSkills(true);
        characterBase.setHealthScale(5D);
        characterBase.setInventoryEquipSlotOrder(Arrays.asList(new EquipedSlotImpl(1), new EquipedSlotImpl(7), new EquipedSlotImpl(5)));
        characterBase.setLastKnownPlayerName("TTest");
        characterBase.setLastReset(new Date());
        characterBase.setMarkedForRemoval(false);
        characterBase.setName("Test");
        characterBase.setUuid(UUID.randomUUID());
        characterBase.setWorld("DIM-1");
        characterBase.setX(10);
        characterBase.setY(15);
        characterBase.setZ(-31);
        characterBase.getUniqueSkillpoints().put("test",
                new HashSet<>(Arrays.asList(new DateKeyPair("uq1"), new DateKeyPair("uq2"))));

        characterBase.setSpellbookPages(new String[][]{
                {"test", "test2", null, "123"},
                {null, null, null, null}
        });

        return characterBase;
    }

    public static void addAttributes(CharacterBase characterBase) {
        Set<BaseCharacterAttribute> attributes = new HashSet<>();
        BaseCharacterAttributeImpl i = new BaseCharacterAttributeImpl();
        i.setCharacterBase(characterBase);
        i.setLevel(5);
        i.setName("str");
        attributes.add(i);
        i = new BaseCharacterAttributeImpl();
        i.setCharacterBase(characterBase);
        i.setLevel(0);
        i.setName("agi");
        attributes.add(i);
        characterBase.setBaseCharacterAttribute(attributes);
    }

    public static void addClasses(CharacterBase characterBase) {
        Set<CharacterClass> classes = new HashSet<>();
        CharacterClass characterClass = new CharacterClassImpl();
        characterClass.setCharacterBase(characterBase);
        characterClass.setExperiences(50.5);
        characterClass.setName("CLass1");
        characterClass.setSkillPoints(5);
        characterClass.setUsedSkillPoints(0);
        characterClass.setCreated(new Date());
        characterClass.setUpdated(new Date());
        classes.add(characterClass);
        characterClass = new CharacterClassImpl();
        characterClass.setCharacterBase(characterBase);
        characterClass.setExperiences(100);
        characterClass.setName("CLass2");
        characterClass.setSkillPoints(10);
        characterClass.setUsedSkillPoints(25);
        characterClass.setCreated(new Date());
        characterClass.setUpdated(new Date());
        characterBase.setCharacterClasses(classes);

        CharacterSkill characterSkill = new CharacterSkillImpl();
        characterSkill.setCharacterBase(characterBase);
        characterSkill.setCooldown(Long.MAX_VALUE);
        characterSkill.setLevel(1);
        characterSkill.setCatalogId("SkillId");
        characterSkill.setFromClass(characterClass);
        characterSkill.setCreated(new Date());
        characterSkill.setUpdated(new Date());
        classes.add(characterClass);


        Set<CharacterSkill> skills = new HashSet<>();
        skills.add(characterSkill);
        characterBase.setCharacterSkills(skills);
    }


    public static class EquipedSlotImpl implements EquipedSlot {

        private int slotIndex = 0;

        public EquipedSlotImpl(int i) {
            this.slotIndex = i;
        }

        @Override
        public int getSlotIndex() {
            return slotIndex;
        }

        @Override
        public String toString() {
            return String.valueOf(slotIndex);
        }
    }
}
