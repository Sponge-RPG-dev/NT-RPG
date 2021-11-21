package cz.neumimto.rpg.common.skills.scripting;

import com.google.inject.Injector;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.inject.Singleton;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
class ScríptParserTests {

    @Inject
    private SkillService skillService;

    @Inject
    private Injector injector;

    @Test
    public void test_01() throws Exception {
        injector.getInstance(TargettedEntity.class);
        injector.getInstance(Exists.class);
        injector.getInstance(A.class);
        injector.getInstance(DamageEntity.class);
        ScriptSkillModel model = new ScriptSkillModel();
        model.id = "aaa";
        model.script = """
                       @target = targetted_entity{range=$settings.range, entityFrom=@caster}
                       IF exists{test=@target}
                          IF damage_entity{damage=20, damaged=@target, damager=@caster}
                             spawn_lighting{location=@target}
                             RETURN OK
                          END
                       END
                       RETURN CANCELLED
                """;

        skillService.skillDefinitionToSkill(model,this.getClass().getClassLoader());

    }

    @Test
    public void test_02() throws Exception {
        injector.getInstance(TargettedEntity.class);
        injector.getInstance(Exists.class);
        injector.getInstance(A.class);
        injector.getInstance(DamageEntity.class);
        ScriptSkillModel model = new ScriptSkillModel();
        model.id = "aaa";
        model.script = """
                       @target = targetted_entity{range=$settings.range, entityFrom=@caster}
                       IF exists{test=@target}
                          IF damage_entity{damage=20, damaged=@target, damager=@caster}
                             DELAY 10000
                               spawn_lighting{location=@target}
                             END
                             RETURN OK
                          END
                       END
                       RETURN CANCELLED
                """;
        skillService.skillDefinitionToSkill(model,this.getClass().getClassLoader());
    }

    @Test
    public void test_03() throws Exception {
        injector.getInstance(TargettedEntity.class);
        injector.getInstance(Exists.class);
        injector.getInstance(A.class);
        injector.getInstance(DamageEntity.class);
        ScriptSkillModel model = new ScriptSkillModel();
        model.id = "aaa";
        model.script = """
                       @target = targetted_entity{range=$settings.range, entityFrom=@caster}
                       IF exists{test=@target}
                          IF damage_entity{damage=20, damaged=@target, damager=@caster}
                             DELAY $settings.cooldown
                               spawn_lighting{location=@target}
                             END
                             RETURN OK
                          END
                       END
                       RETURN CANCELLED
                """;
        skillService.skillDefinitionToSkill(model,this.getClass().getClassLoader());
    }

    @Singleton
    public static class TargettedEntity {

        public IEntity get(int range, IEntity entity) {
            return null;
        }
    }

    @Singleton
    public static class DamageEntity {

        public boolean damage(double damage, IEntity entity, IEntity damager) {
            return true;
        }
    }

    @Singleton
    public static class Exists {

        public boolean test(Object o) {
            return o != null;
        }
    }

    @Singleton
    public static class A {

        public void spawn(IEntity o) {
            int i = 1;
        }
    }
}
