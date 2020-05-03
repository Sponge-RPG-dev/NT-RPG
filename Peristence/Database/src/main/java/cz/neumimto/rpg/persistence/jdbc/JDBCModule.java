package cz.neumimto.rpg.persistence.jdbc;

import com.google.inject.Injector;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistence.jdbc.dao.JDBCCharacterClassDao;
import cz.neumimto.rpg.persistence.jdbc.dao.JdbcPlayerDao;
import cz.neumimto.rpg.persistence.jdbc.migrations.DbMigrationsService;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;


public class JDBCModule implements RpgAddon {

    @Override
    public Map<Class<?>, Class<?>> getBindings() {
        Map bindings = new HashMap<>();
        bindings.put(ICharacterClassDao.class, JDBCCharacterClassDao.class);
        bindings.put(IPlayerDao.class, JdbcPlayerDao.class);
        bindings.put(IPersistenceHandler.class, JDBCersistenceHandler.class);

        return bindings;
    }

    @Override
    public Map<Class<?>, ?> getProviders(Map<String, Object> implementationScope) {
        Map map = new HashMap();
        if (implementationScope.containsKey("DATASOURCE")) {
            map.put(DataSource.class, implementationScope.get("DATASOURCE"));
        } else {
            Path props = getOrCreateDatabaseProperties((String) implementationScope.get("WORKINGDIR"));
            Properties properties = new Properties();
            try (FileReader fileReader = new FileReader(props.toFile())) {
                properties.load(fileReader);

                HikariConfig cfg = new HikariConfig();
                cfg.setUsername(getAndLog(properties, "username"));
                cfg.setJdbcUrl(getAndLog(properties, "connection"));
                cfg.setPassword(properties.getProperty("password"));
                DataSource ds = new HikariDataSource(cfg);
                map.put(DataSource.class, ds);
            } catch (IOException e) {
                Log.error("Could not read database.properties file", e);
            }

        }
        return map;
    }

    private String getAndLog(Properties properties, String property) {
        if (properties.containsKey(property)) {
            Log.info("Setting up DataSource - " + property + "=" + properties.get(property));
            return properties.getProperty(property);
        }
        return "";
    }

    @Override
    public void processStageEarly(Injector injector) {
        DataSource instance = injector.getInstance(DataSource.class);
        processMigrations(instance);
    }

    public void processMigrations(DataSource dataSource) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DbMigrationsService dbMigrationsService = new DbMigrationsService();
            dbMigrationsService.setConnection(connection);
            dbMigrationsService.startMigration();
        } catch (Exception e) {
            Log.error("Could not process db migration", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected Path getOrCreateDatabaseProperties(String string) {
        Path path = Paths.get(string, "database.properties");
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("database.properties");
            try {
                Files.copy(resourceAsStream, path);
                info("File \"database.properties\" has been copied into the config/nt-rpg folder.");
                info("\u001b[1;32mBy default H2 databse will be used");
            } catch (IOException e) {
                error("Could not create a database.properties file ", e);
            }
        }
        return path;
    }
}
