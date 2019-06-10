package cz.neumimto.rpg.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLClassLoader;

public abstract class IResourceLoader {
    public abstract Object loadClass(Class<?> clazz) throws IllegalAccessException, InstantiationException;

    public abstract URLClassLoader getConfigClassLoader();

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Skill {

        String value();

        boolean dynamicLocalizationNodes() default true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ModelMapper {

    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ListenerClass {

    }
}
