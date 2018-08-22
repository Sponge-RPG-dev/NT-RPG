package cz.neumimto.rpg.effects;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Generate {
    String id();

    boolean inject() default false;

    @Retention(RetentionPolicy.RUNTIME)
    @interface Constructor {

    }

    String description();
}
