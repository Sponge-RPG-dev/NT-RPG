package cz.neumimto.rpg.persistance.dao;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import cz.neumimto.rpg.persistance.migrations.DbMigrationsService;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDbBootstrap {

    public static final int port = 50978;
    public static DB db;

    private static DBConfigurationBuilder config;

    public static void initializeDatabase() throws ManagedProcessException {
        config = DBConfigurationBuilder.newBuilder();
        config.setPort(port);
        db = DB.newEmbeddedDB(config.build());
        db.start();
    }

    public static void tearDown() throws ManagedProcessException {
        db.stop();
    }

    public static void runMigrations() throws SQLException {
        DbMigrationsService dbMigrationsService = new DbMigrationsService();
        dbMigrationsService.setConnection(DriverManager.getConnection(config.getURL("NtRpgTest"), "root", ""));

        dbMigrationsService.addMigration("");
    }
 }
