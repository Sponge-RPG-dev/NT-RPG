package cz.neumimto.configuration;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationContainer {
    String path() default "";

    String filename() default "";
}