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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Test
    public void testParamParsing() {
        Collection<String> values = super.filterMechanicParams(new TestM01(),
                "testm01 settings.test=settings.customNode effect=Effect(cz.neumimto.rpg.effects.TestEffectVoid, target, settings.effect_duration) name='Test Test'")
                .keySet();


        Assert.assertEquals(7, values.size());
        Assert.assertTrue(values.contains("'Test Test'"));
        Assert.assertTrue(values.contains("caster"));
        Assert.assertTrue(values.contains("target"));
        Assert.assertTrue(values.contains("this"));
        Assert.assertTrue(values.contains("new cz.neumimto.rpg.effects.TestEffectVoid( target, effect_duration)"));
        Assert.assertTrue(values.contains("settings.damage"));
        Assert.assertTrue(values.contains("settings.customNode"));
    }

    public static class TestM01 {

        @Handler
        public void testMethod(@Caster Object o,
                               @SkillArgument("settings.test") int n,
                               @Target Object w,
                               @StaticArgument("name") String k,
                               @SkillArgument("settings.damage") int i,
                               ISkill skill,
                               @SkillArgument("effect") IEffect effect) {

        }
    }

}
