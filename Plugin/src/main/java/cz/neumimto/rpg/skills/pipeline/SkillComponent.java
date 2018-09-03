package cz.neumimto.rpg.skills.pipeline;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by NeumimTo on 4.8.2018.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SkillComponent {
    String value();

    Param[] params();

    String usage();

    @interface Param {
        String value();
    }
}
