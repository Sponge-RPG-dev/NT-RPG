package cz.neumimto;

import cz.neumimto.effects.positive.CriticalEffect;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IGlobalEffect;
import javassist.CannotCompileException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 3.3.2018.
 */
public class GETests {

    @Test
    public void testGen() throws IllegalAccessException, CannotCompileException, InstantiationException {
        ClassGenerator classGenerator = new ClassGenerator();

        IGlobalEffect<? extends IEffect> iGlobalEffect = classGenerator.generateGlobalEffect(CriticalEffect.class);
        Map<String, String> mult = new HashMap<>();
        mult.put("mult", "0.4x");
        mult.put("chance","100%");
        IEffect construct = iGlobalEffect.construct(null, -1, mult);
        construct.getValue();
    }

}
