package cz.neumimto.rpg.persistence.jdbc.dao;

import cz.neumimto.persistence.TestHelper;
import cz.neumimto.rpg.api.RpgJdbcTests;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JdbcPlayerDaoTest {

    private static JdbcPlayerDao jdbcPlayerDao;

    @BeforeAll
    public static void before() {
        new RpgJdbcTests();
        try {
            MariaDbBootstrap.runMigrations();
            jdbcPlayerDao = new JdbcPlayerDao(MariaDbBootstrap.ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        CharacterBase characterBase = TestHelper.createCharacterBase();
        CharacterBase loadded = null;
        try {
            jdbcPlayerDao.create(characterBase);
            loadded = jdbcPlayerDao.getCharacter(characterBase.getUuid(), characterBase.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(characterBase.getId());
        Assertions.assertNotNull(loadded);

        Assertions.assertEquals(characterBase.getAttributePoints(), loadded.getAttributePoints());
        Assertions.assertEquals(characterBase.getAttributePointsSpent(), loadded.getAttributePointsSpent());
        Assertions.assertEquals(characterBase.getAttributes(), loadded.getAttributes());
        Assertions.assertEquals(characterBase.getBaseCharacterAttribute(), loadded.getBaseCharacterAttribute());
        Assertions.assertEquals(characterBase.getCharacterClasses(), loadded.getCharacterClasses());
        Assertions.assertEquals(characterBase.getCharacterSkills(), loadded.getCharacterSkills());
        Assertions.assertEquals(characterBase.getHealthScale(), loadded.getHealthScale());
        Assertions.assertEquals(characterBase.getId(), loadded.getId());
        Assertions.assertEquals(characterBase.getInfo(), loadded.getInfo());


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
    }



    @AfterAll
    public static void stop() {
        MariaDbBootstrap.tearDown();
    }
}