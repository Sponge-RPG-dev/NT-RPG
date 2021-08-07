package cz.neumimto.rpg.common.scripting;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.entity.TestCharacter;
import cz.neumimto.rpg.common.skills.scripting.*;
import cz.neumimto.rpg.effects.TestEffectFloat;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.util.Collection;

@Singleton
public class TestCustomSkillGenerator extends CustomSkillGenerator {
    @Override
    protected Object translateDamageType(String damageType) {
        return "null";
    }

    @Override
    protected String getDefaultEffectPackage() {
        return TestEffectFloat.class.getPackage().getName();
    }

    @Override
    protected Type characterClassImpl() {
        return TestCharacter.class;
    }

    @Override
    protected Class<?> targeted() {
        return Void.class;
    }


    public static class TestM01 {

        @Handler
        public void testMethod(@Caster Object o,
                               @SkillArgument("settings.test") int n,
                               Object w,
                               @StaticArgument("name") String k,
                               @SkillArgument("settings.damage") int i,
                               ISkill skill,
                               @SkillArgument("effect") IEffect effect) {

        }
    }

}
