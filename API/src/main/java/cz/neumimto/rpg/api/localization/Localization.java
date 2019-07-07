package cz.neumimto.rpg.api.localization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Localization {
    String[] value();
}
