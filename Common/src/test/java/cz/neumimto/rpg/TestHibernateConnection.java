package cz.neumimto.rpg;


import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.persistance.model.JPABaseCharacterAttribute;
import cz.neumimto.rpg.common.persistance.model.JPACharacterBase;
import cz.neumimto.rpg.common.persistance.model.JPACharacterClass;
import cz.neumimto.rpg.common.persistance.model.JPACharacterSkill;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class TestHibernateConnection {

    public static SessionFactory get() {
        Log.info("Initializing Hibernate .... ");
        Path p = Paths.get(Rpg.get().getWorkingDirectory(), "test-database.properties");

        File file = p.toFile();
        Properties properties = new Properties();
        try (FileInputStream stream = new FileInputStream(file)) {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        properties.put(Environment.ARTIFACT_PROCESSING_ORDER, "class, hbm");
        properties.put(Environment.ENABLE_LAZY_LOAD_NO_TRANS, true);


        if (!properties.containsKey(Environment.HBM2DDL_AUTO)) {
            properties.put(Environment.HBM2DDL_AUTO, "validate");
        }
        properties.put(Environment.LOG_SESSION_METRICS, false);
        String s = (String) properties.get("hibernate.connection.url");
        if (s == null) {
            throw new RuntimeException("hibernate.connection.url is missing in database.properties file");
        }

        String className = properties.get("hibernate.connection.driver_class").toString();
        try {
            Log.info("Loading driver class " + className);
            TestHibernateConnection.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            Log.error("====================================================");
            Log.error("Class " + className + " not found on the classpath! ");
            Log.error("Possible causes: ");
            Log.error("       - The database driver is not on the classpath");
            Log.error("       - The classname is miss spelled");
            Log.error("====================================================");
        }
        Configuration configuration = new Configuration();
        configuration.addProperties(properties);
        configuration.addAnnotatedClass(JPACharacterClass.class);
        configuration.addAnnotatedClass(JPACharacterSkill.class);
        configuration.addAnnotatedClass(JPACharacterBase.class);
        configuration.addAnnotatedClass(JPABaseCharacterAttribute.class);

        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        try {
            return configuration.buildSessionFactory(registry);
        } catch (Exception e) {
            throw new RuntimeException("Could not build session factory", e);
        }

    }
}