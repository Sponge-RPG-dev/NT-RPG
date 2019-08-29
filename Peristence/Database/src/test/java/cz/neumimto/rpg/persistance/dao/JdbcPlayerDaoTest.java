package cz.neumimto.rpg.persistance.dao;

import ch.vorburger.exec.ManagedProcessException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.neumimto.rpg.api.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttributeImpl;
import cz.neumimto.rpg.persistance.model.CharacterBaseImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.*;

public class JdbcPlayerDaoTest {

    private static DataSource dataSource;
    private static JdbcPlayerDao jdbcPlayerDao;
    @BeforeAll
    public static void before() {
        try {
            MariaDbBootstrap.initializeDatabase();
            MariaDbBootstrap.runMigrations();


            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl("jdbc:mysql://localhost:"+MariaDbBootstrap.port+"/"+MariaDbBootstrap.NAME);
            cfg.setPassword("");
            cfg.setUsername("root");

            dataSource = new HikariDataSource(cfg);
            jdbcPlayerDao = new JdbcPlayerDao(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
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
        try {
            jdbcPlayerDao.create(characterBase);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(characterBase.getId());

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
    public static void stop() throws ManagedProcessException {
        MariaDbBootstrap.tearDown();
    }
}