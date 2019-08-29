package cz.neumimto.rpg.persistance;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistance.dao.JDBCCharacterClassDao;
import cz.neumimto.rpg.persistance.dao.JdbcPlayerDao;
import cz.neumimto.rpg.persistance.migrations.DbMigrationsService;

import javax.sql.DataSource;
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


public class JPAModule implements RpgAddon {

    @Override
    public Map<Class<?>, Class<?>> getBindings() {
        Map bindings = new HashMap<>();
        bindings.put(ICharacterClassDao.class, JDBCCharacterClassDao.class);
        bindings.put(IPlayerDao.class, JdbcPlayerDao.class);
        bindings.put(IPersistenceHandler.class, JPAPersistenceHandler.class);

        return bindings;
    }

    public void setup(DataSource dataSource) {
        info("Initializing Database Persistance Module .... ");

        processMigrations(dataSource);

    }

    public void loadDriver(Properties properties) {
        String className = properties.get("hibernate.connection.driver_class").toString();
        try {

            info("Loading driver class " + className);
            getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            error("====================================================");
            error("Class " + className + " not found on the classpath! ");
            error("Possible causes: ");
            error("       - The database driver is not on the classpath");
            error("       - The classname is miss spelled");
            error("====================================================");
        }
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

    protected Path getOrCreateDatabaseProperties() {
        Path path = Paths.get(Rpg.get().getWorkingDirectory(), "database.properties");
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
