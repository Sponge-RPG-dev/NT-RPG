package cz.neumimto.rpg.common.skills.scripting;

import org.junit.jupiter.api.Test;

class ScríptParserTests {

    @Test
    public void test_01() {
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
        assert lex.settingsVar().contains("range");

    }

}