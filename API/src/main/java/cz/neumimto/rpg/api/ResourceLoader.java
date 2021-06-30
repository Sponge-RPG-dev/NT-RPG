package cz.neumimto.rpg.api;

import cz.neumimto.rpg.api.skills.ISkill;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

public interface ResourceLoader {

    void init();

    void loadServices();

    ISkill loadSkillClass(Class<? extends ISkill> clazz);

    void reloadLocalizations(Locale locale);

    void loadExternalJars();

    //Set<RpgAddon> discoverGuiceModules();

    @Retention(RetentionPolicy.RUNTIME)
    @interface Skill {

        String value();

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
