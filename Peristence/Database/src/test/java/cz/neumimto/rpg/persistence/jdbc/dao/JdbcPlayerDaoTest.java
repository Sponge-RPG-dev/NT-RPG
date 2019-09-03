package cz.neumimto.rpg.persistence.jdbc.dao;

import cz.neumimto.rpg.api.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.persistence.model.BaseCharacterAttributeImpl;
import cz.neumimto.rpg.persistence.model.CharacterBaseImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

public class JdbcPlayerDaoTest {

    private static JdbcPlayerDao jdbcPlayerDao;

    @BeforeAll
    public static void before() {
        try {
            MariaDbBootstrap.runMigrations();
            jdbcPlayerDao = new JdbcPlayerDao(MariaDbBootstrap.ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        CharacterBase characterBase = createCharacterBase();
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
        Assertions.assertEquals(characterBase.getInventoryEquipSlotOrder(), loadded.getInventoryEquipSlotOrder());
        Assertions.assertEquals(characterBase.getLastKnownPlayerName(), loadded.getLastKnownPlayerName());
        Assertions.assertEquals(characterBase.getLastReset(), loadded.getLastReset());
        Assertions.assertEquals(characterBase.getMarkedForRemoval(), loadded.getMarkedForRemoval());
        Assertions.assertEquals(characterBase.getName(), loadded.getName());
        Assertions.assertEquals(characterBase.getUuid(), loadded.getUuid());
        Assertions.assertEquals(characterBase.getWorld(), loadded.getWorld());
        Assertions.assertEquals(characterBase.getX(), loadded.getX());
        Assertions.assertEquals(characterBase.getY(), loadded.getY());
        Assertions.assertEquals(characterBase.getZ(), loadded.getZ());

    }

    private CharacterBaseImpl createCharacterBase() {
        CharacterBaseImpl characterBase = new CharacterBaseImpl();
        characterBase.setId(null);
        characterBase.setAttributePoints(1);
        characterBase.setAttributePointsSpent(2);
        characterBase.setCanResetskills(true);
        characterBase.setHealthScale(5D);
        characterBase.setInfo("Info");
        characterBase.setInventoryEquipSlotOrder(Arrays.asList(() -> 1, () -> 7, () -> 5));
        characterBase.setLastKnownPlayerName("TTest");
        characterBase.setLastReset(new Date());
        characterBase.setMarkedForRemoval(false);
        characterBase.setName("Test");
        characterBase.setUuid(UUID.randomUUID());
        characterBase.setWorld("DIM-1");
        characterBase.setX(10);
        characterBase.setY(15);
        characterBase.setZ(-31);
        return characterBase;
    }

    public void fillAttributes(CharacterBase characterBase) {
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

    @AfterAll
    public static void stop() {
        MariaDbBootstrap.tearDown();
    }
}