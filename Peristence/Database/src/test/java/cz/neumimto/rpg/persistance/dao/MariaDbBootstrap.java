package cz.neumimto.rpg.persistance.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.neumimto.rpg.persistance.migrations.DbMigrationsService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Scanner;

public class MariaDbBootstrap {

    public static final int port = 3306;
    public static HikariDataSource ds;
    public static final String NAME = "RpgTesting";

    public static void initializeDataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:mysql://localhost:"+MariaDbBootstrap.port+"/"+MariaDbBootstrap.NAME);
        cfg.setPassword("chleba");
        cfg.setUsername("root");
        ds = new HikariDataSource(cfg);
    }

    public static void tearDown() {
        ds.close();
    }

    public static void runMigrations() {
        try {
            initializeDataSource();
            DbMigrationsService dbMigrationsService = new DbMigrationsService();
            dbMigrationsService.setConnection(ds.getConnection());

            runMigration(dbMigrationsService, "sql/mysql/040918-init-db.sql");
            runMigration(dbMigrationsService, "sql/mysql/060119-update-2.0.0.sql");
            runMigration(dbMigrationsService, "sql/mysql/240219-fix-null-levels.sql");
            runMigration(dbMigrationsService, "sql/mysql/250619-attrpoints-spent.sql");

            dbMigrationsService.startMigration();
        } catch (SQLException | IOException e) {
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
