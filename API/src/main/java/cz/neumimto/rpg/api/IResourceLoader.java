package cz.neumimto.rpg.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLClassLoader;
import java.util.Map;

public interface IResourceLoader {

    Object loadClass(Class<?> clazz) throws IllegalAccessException, InstantiationException;

    URLClassLoader getConfigClassLoader();

    Map<String, URLClassLoader> getClassLoaderMap();

    @Retention(RetentionPolicy.RUNTIME)
    @interface Skill {

        String value();

        boolean dynamicLocalizationNodes() default true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Command {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface ModelMapper {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface ListenerClass {

    }
}
