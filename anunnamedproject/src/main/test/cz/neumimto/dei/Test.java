package cz.neumimto.dei;

import cz.neumimto.core.FindPersistenceContextEvent;
import cz.neumimto.core.SessionFactoryCreatedEvent;
import cz.neumimto.core.ioc.IoC;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by ja on 5.7.2016.
 */

public class Test {

    public static SessionFactory sessionFactory;

    @BeforeClass
    public static void setupHibernate() throws URISyntaxException {

        Properties properties = new Properties();
        URL resource = Test.class.getClassLoader().getResource("database.properties");
        try {
            FileInputStream ev = new FileInputStream(new File(resource.toURI()));
            Throwable configuration = null;

            try {
                properties.load(ev);
            } catch (Throwable var19) {
                configuration = var19;
                throw var19;
            } finally {
                if(ev != null) {
                    if(configuration != null) {
                        try {
                            ev.close();
                        } catch (Throwable var18) {
                            configuration.addSuppressed(var18);
                        }
                    } else {
                        ev.close();
                    }
                }

            }
        } catch (IOException var21) {
            var21.printStackTrace();
        }

        properties.put("hibernate.mapping.precedence", "class ,hbm");
        FindPersistenceContextEvent ev1 = new FindPersistenceContextEvent();
        try {
            new DEI().onFindPersistentContext(ev1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Configuration configuration1 = new Configuration();
        configuration1.addProperties(properties);
        ev1.getClasses().stream().forEach(configuration1::addAnnotatedClass);

        try {
            Test.class.getClassLoader().loadClass(properties.get("hibernate.hikari.dataSourceClassName").toString());
        } catch (ClassNotFoundException var17) {
            var17.printStackTrace();
        }

        try {
            Class.forName(properties.get("hibernate.hikari.dataSourceClassName").toString());
            Class.forName(properties.get("hibernate.connection.provider_class").toString());
        } catch (ClassNotFoundException var16) {
            var16.printStackTrace();
        }

        StandardServiceRegistry registry = (new StandardServiceRegistryBuilder()).applySettings(configuration1.getProperties()).build();
        SessionFactory factory = configuration1.buildSessionFactory(registry);
        IoC.get().registerInterfaceImplementation(SessionFactory.class, factory);
        SessionFactoryCreatedEvent e = new SessionFactoryCreatedEvent(factory);
        sessionFactory = e.getSessionFactory();
    }
}
