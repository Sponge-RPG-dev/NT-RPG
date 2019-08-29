package cz.neumimto.rpg.persistance.dao;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import cz.neumimto.rpg.persistance.migrations.DbMigrationsService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class MariaDbBootstrap {

    public static final int port = 50978;
    public static DB db;

    private static DBConfigurationBuilder config;

    public static void initializeDatabase() throws ManagedProcessException {
        config = DBConfigurationBuilder.newBuilder();
        config.setPort(port);
        db = DB.newEmbeddedDB(config.build());
        db.start();
        db.createDB("NtRpgTest");
    }

    public static void tearDown() throws ManagedProcessException {
        db.stop();
    }

    public static void runMigrations() throws SQLException {
        DbMigrationsService dbMigrationsService = new DbMigrationsService();
        dbMigrationsService.setConnection(DriverManager.getConnection(config.getURL("NtRpgTest"), "root", ""));

        runMigration(dbMigrationsService, "sql/mysql/040918-init-db.sql");
        runMigration(dbMigrationsService, "sql/mysql/060119-update-2.0.0.sql");
        runMigration(dbMigrationsService, "sql/mysql/240219-fix-null-levels.sql");
        runMigration(dbMigrationsService, "sql/mysql/250619-attrpoints-spent.sql");
        try {
            dbMigrationsService.startMigration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runMigration(DbMigrationsService dbMigrationsService, String string) {
        ClassLoader classLoader = MariaDbBootstrap.class.getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream(string);
        dbMigrationsService.addMigration(toString(resourceAsStream));
    }

    static String toString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
