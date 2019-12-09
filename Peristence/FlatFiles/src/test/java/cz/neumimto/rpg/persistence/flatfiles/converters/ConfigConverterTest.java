package cz.neumimto.rpg.persistence.flatfiles.converters;

import cz.neumimto.persistence.TestHelper;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgTests;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.persistence.flatfiles.dao.FlatFilePlayerDao;
import cz.neumimto.rpg.persistence.model.CharacterBaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

class ConfigConverterTest {


    @BeforeAll
    public static void before() {
        new RpgTests();
        new File(Rpg.get().getWorkingDirectory()).mkdirs();

    }

    @Test
    public void testCharacterLoadAndSave() {
        CharacterBaseImpl characterBase = TestHelper.createCharacterBase();

        FlatFilePlayerDao flatFilePlayerDao = new FlatFilePlayerDao();
        flatFilePlayerDao.create(characterBase);

        CharacterBase loadded = flatFilePlayerDao.getCharacter(characterBase.getUuid(), characterBase.getName());

        Assertions.assertEquals(characterBase.getAttributePoints(), loadded.getAttributePoints());
        Assertions.assertEquals(characterBase.getAttributePointsSpent(), loadded.getAttributePointsSpent());
        Assertions.assertEquals(characterBase.getAttributes(), loadded.getAttributes());
        Assertions.assertEquals(characterBase.getBaseCharacterAttribute(), loadded.getBaseCharacterAttribute());
        Assertions.assertEquals(characterBase.getCharacterClasses(), loadded.getCharacterClasses());
        Assertions.assertEquals(characterBase.getCharacterSkills(), loadded.getCharacterSkills());
        Assertions.assertEquals(characterBase.getHealthScale(), loadded.getHealthScale());
        Assertions.assertEquals(characterBase.getId(), loadded.getId());

        Assertions.assertEquals(characterBase.getLastKnownPlayerName(), loadded.getLastKnownPlayerName());
        Assertions.assertEquals(characterBase.getMarkedForRemoval(), loadded.getMarkedForRemoval());
        Assertions.assertEquals(characterBase.getName(), loadded.getName());
        Assertions.assertEquals(characterBase.getUuid(), loadded.getUuid());
        Assertions.assertEquals(characterBase.getWorld(), loadded.getWorld());
        Assertions.assertEquals(characterBase.getX(), loadded.getX());
        Assertions.assertEquals(characterBase.getY(), loadded.getY());
        Assertions.assertEquals(characterBase.getZ(), loadded.getZ());

        if (characterBase.getInventoryEquipSlotOrder() != null) {
            Assertions.assertEquals(characterBase.getInventoryEquipSlotOrder().size(), loadded.getInventoryEquipSlotOrder().size());
            for (int i = 0; i < characterBase.getInventoryEquipSlotOrder().size(); i++) {
                Assertions.assertEquals(characterBase.getInventoryEquipSlotOrder().get(i).getSlotIndex(), loadded.getInventoryEquipSlotOrder().get(i).getSlotIndex());
            }
        }

        TestHelper.addClasses(characterBase);
        flatFilePlayerDao.update(characterBase);
        loadded = flatFilePlayerDao.getCharacter(characterBase.getUuid(), characterBase.getName());
        for (CharacterClass characterClass : characterBase.getCharacterClasses()) {
            Optional<CharacterClass> first = loadded.getCharacterClasses().stream().filter(a -> a.getName().equals(characterClass.getName())).findFirst();
            if (!first.isPresent()) {
                throw new IllegalStateException("");
            }
            CharacterClass loadedClass = first.get();

            Assertions.assertNotNull(loadedClass.getId());

            Assertions.assertEquals(characterClass.getCharacterBase().getId(), loadedClass.getCharacterBase().getId());
            Assertions.assertEquals(characterClass.getExperiences(), loadedClass.getExperiences());
            Assertions.assertEquals(characterClass.getLevel(), loadedClass.getLevel());
            Assertions.assertEquals(characterClass.getSkillPoints(), loadedClass.getSkillPoints());
            Assertions.assertEquals(characterClass.getUsedSkillPoints(), loadedClass.getUsedSkillPoints());
        }
    }

}