package cz.neumimto.rpg.reloading;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Reload {
    String on();
}
