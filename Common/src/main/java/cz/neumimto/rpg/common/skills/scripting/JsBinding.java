package cz.neumimto.rpg.common.skills.scripting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsBinding {

    Type value();

    enum Type {
        CLASS,
        OBJECT,
        CONTAINER
    }
}
