package cz.neumimto.rpg.common.skills.scripting;

import com.google.inject.Injector;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.common.scripting.TestCustomSkillGenerator;
import cz.neumimto.rpg.junit.CharactersExtension;
import cz.neumimto.rpg.junit.NtRpgExtension;
import cz.neumimto.rpg.junit.TestGuiceModule;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@ExtendWith({GuiceExtension.class, NtRpgExtension.class})
@IncludeModule(TestGuiceModule.class)
class ScríptParserTests {

    @Inject
    private TestCustomSkillGenerator skillGenerator;

    @Inject
    private Injector injector;

    @Test
    public void test_01() {
        Set<Object> mechanics = new HashSet<>();
        injector.getInstance(TargettedEntity.class);
        injector.getInstance(Exists.class);
        injector.getInstance(A.class);
        injector.getInstance(DamageEntity.class);
        Parser.ParserOutput lex = new Parser().parse("""
                     @target = targetted_entity{range=$settings.range, entityFrom=@caster}
                       IF exists{test=@target}
                          IF damage_entity{damage=20, damaged=@target, damager=@caster}
                             spawn_lighting{location=@target}
                             RETURN OK
                          END
                       END
                       RETURN CANCELLED
                """);

        assert lex.requiredMechanics().contains("damage_entity");
        assert lex.requiredMechanics().contains("targetted_entity");
        assert lex.requiredMechanics().contains("spawn_lighting");
        assert lex.requiredMechanics().contains("exists");
    }

    @Test
    public void test_02() throws Exception {
        injector.getInstance(TargettedEntity.class);
        injector.getInstance(Exists.class);
        injector.getInstance(A.class);
        injector.getInstance(DamageEntity.class);
        ScriptSkillModel model = new ScriptSkillModel();
        model.setId("aaa");
        model.setScript("""
                       @target = targetted_entity{range=$settings.range, entityFrom=@caster}
                       IF exists{test=@target}
                          IF damage_entity{damage=20, damaged=@target, damager=@caster}
                             spawn_lighting{location=@target}
                             RETURN OK
                          END
                       END
                       RETURN CANCELLED
                """);
        skillGenerator.generate(model,this.getClass().getClassLoader());
    }

    @Singleton
    @SkillMechanic("targetted_entity")
    public static class TargettedEntity {

        @Handler
        public IEntity get(@SkillArgument("range") int range, @SkillArgument("entityFrom")IEntity entity) {
            return null;
        }
    }

    @Singleton
    @SkillMechanic("damage_entity")
    public static class DamageEntity {

        @Handler
        public boolean damage(@SkillArgument("damage") double damage, @SkillArgument("damaged")IEntity entity, @SkillArgument("damager")IEntity damager) {
            return true;
        }
    }

    @Singleton
    @SkillMechanic("exists")
    public static class Exists {

        @Handler
        public boolean test(@SkillArgument("test") Object o) {
            return o != null;
        }
    }

    @Singleton
    @SkillMechanic("spawn_lighting")
    public static class A {

        @Handler
        public void test(@SkillArgument("target") IEntity o) {
            int i = 1;
        }
    }
}
