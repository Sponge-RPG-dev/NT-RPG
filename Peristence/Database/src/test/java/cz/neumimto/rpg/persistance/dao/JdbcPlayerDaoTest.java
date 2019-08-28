package cz.neumimto.rpg.persistance.dao;

import ch.vorburger.exec.ManagedProcessException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;


class JdbcPlayerDaoTest {

    @BeforeAll
    public static void before() throws ManagedProcessException, SQLException {
        MariaDbBootstrap.initializeDatabase();
        MariaDbBootstrap.runMigrations();
    }

    @Test
    public void test() {
        int i = 0;
    }

    @AfterAll
    public static void stop() throws ManagedProcessException {
        MariaDbBootstrap.tearDown();
    }
}