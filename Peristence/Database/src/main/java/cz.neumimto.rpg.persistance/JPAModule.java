package cz.neumimto.rpg.persistance;

import com.google.inject.Provider;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.RpgAddon;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistance.dao.JPACharacterClassDao;
import cz.neumimto.rpg.persistance.dao.JdbcPlayerDao;
import cz.neumimto.rpg.persistance.migrations.DbMigrationsService;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttributeImpl;
import cz.neumimto.rpg.persistance.model.CharacterBaseImpl;
import cz.neumimto.rpg.persistance.model.CharacterClassImpl;
import cz.neumimto.rpg.persistance.model.CharacterSkillImpl;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
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
        bindings.put(ICharacterClassDao.class, JPACharacterClassDao.class);
        bindings.put(IPlayerDao.class, JdbcPlayerDao.class);
        bindings.put(IPersistenceHandler.class, JPAPersistenceHandler.class);
        final SessionFactory sessionFactory = setupHibernate();

        Provider<SessionFactory> providerSF = () -> sessionFactory;
        bindings.put(SessionFactory.class, providerSF);

        return bindings;
    }

    public SessionFactory setupHibernate() {
        info("Initializing Hibernate .... ");

        Path p = getOrCreateDatabaseProperties();
        Properties properties = loadDatabaseProperties(p);
        processMigrations(properties);

        Configuration configuration = new Configuration();
        configuration.addProperties(properties);
        addJPAClasses(configuration);

        loadDriver(properties);

        return createSessionFactory(configuration);
    }

    public SessionFactory createSessionFactory(Configuration configuration) {
        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        SessionFactory factory = null;
        try {
            factory = configuration.buildSessionFactory(registry);
        } catch (Exception e) {
            error("Could not build session factory", e);
            error("^ This is the relevant part of log you are looking for");
            throw new RuntimeException("Cannot connect to database");
        }

        return factory;
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

    public void processMigrations(Properties properties) {
        Connection connection = null;
        try {
            String connUrl = (String) properties.get("hibernate.connection.url");

            connection = DriverManager.getConnection(connUrl, properties.getProperty(Environment.USER), properties.getProperty(Environment.PASS));
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

    public void addJPAClasses(Configuration configuration) {
        configuration.addAnnotatedClass(CharacterClassImpl.class);
        configuration.addAnnotatedClass(CharacterBaseImpl.class);
        configuration.addAnnotatedClass(BaseCharacterAttributeImpl.class);
        configuration.addAnnotatedClass(CharacterSkillImpl.class);
    }

    public Properties loadDatabaseProperties(Path p) {
        Properties properties = new Properties();
        try (FileInputStream stream = new FileInputStream(p.toFile())) {
            properties.load(stream);
             /*
            I dont want these to be changeable from config file, so just set them every time
            */
            properties.put(Environment.ARTIFACT_PROCESSING_ORDER, "class, hbm");
            properties.put(Environment.ENABLE_LAZY_LOAD_NO_TRANS, true);

            /*
            Dont override if setup otherwise
            */
            if (!properties.containsKey(Environment.HBM2DDL_AUTO)) {
                properties.put(Environment.HBM2DDL_AUTO, "validate");
            }
            properties.put(Environment.LOG_SESSION_METRICS, false);
            properties.put(Environment.LOG_JDBC_WARNINGS, false);

            if (!properties.contains("hibernate.connection.url")) {
                throw new InvalidDatabaseConfigFileException("hibernate.connection.url is missing in database.properties file");
            }
        } catch (IOException e) {
            Log.error("Could not read database.properties file", e);
        }

        return properties;
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
