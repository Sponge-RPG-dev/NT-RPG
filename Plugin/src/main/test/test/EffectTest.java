package test;

import cz.neumimto.ClassGenerator;
import cz.neumimto.effects.EffectBase;
import cz.neumimto.effects.IEffectConsumer;
import cz.neumimto.effects.IGlobalEffect;

@ClassGenerator.Generate(id = "name", inject = true)
public class EffectTest extends EffectBase {

    public static IGlobalEffect<EffectTest> global = null;

    public static String name = "testeffect";
    public long l;

    public EffectTest(IEffectConsumer c, long duration, float level) {

    }

    public EffectTest() {
        setPeriod(100);
        setDuration(50000);
    }

    @Override
    public void onTick() {
        l++;
    }
}
