package cz.neumimto.rpg;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.model.EffectModelFactory;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 6.1.2018.
 */
public class EffectFactoryTest {

    public class A extends EffectBase {

        @Inject
        public A(String name, IEffectConsumer consumer, @Inject Integer i) {
            super(name, consumer);
            setValue(i);
        }

    }

    public class B extends EffectBase {

        public B(String name, IEffectConsumer consumer, @Inject Integer i) {
            super(name, consumer);
            setValue(i);
        }

    }

    public class C extends EffectBase {

        public C(String name, IEffectConsumer consumer, @Inject TestModel g) {
            super(name, consumer);
            setValue(g);
        }
    }

    @Test
    public void q() {
        Map<String, String> g = new HashMap<>();
        g.put("Q","1234");
        Integer integer = EffectModelFactory.create(A.class, g, Integer.class);
        assert integer.equals(1234);
        Integer integer1 = EffectModelFactory.create(B.class, g, Integer.class);
        assert integer1.equals(1234);
        Map<String, String> w = new HashMap<>();
        w.put("l", "1%");
        w.put("v", "-1.2%");
        w.put("q", "q");
        TestModel testModel = EffectModelFactory.create(C.class,w , TestModel.class);
        assert testModel.l == 1;
        assert testModel.v == -1.2;
        assert testModel.q.equalsIgnoreCase("q");
        assert testModel.w == null;
    }

}
