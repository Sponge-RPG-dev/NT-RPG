package test;
import cz.neumimto.ClassGenerator;
import cz.neumimto.effects.EffectBase;
import cz.neumimto.effects.IEffectConsumer;
import cz.neumimto.effects.IGlobalEffect;

/**
 * Created by fs on 12.10.15.
 */
@ClassGenerator.Generate(id = "name", inject = true)
public class EffectTest extends EffectBase {

    public static IGlobalEffect<EffectTest> global = null;

    public static String name = "testeffect";

    public EffectTest(IEffectConsumer c, long duration, float level) {

    }

    public EffectTest() {
        setPeriod(100);
        setDuration(50000);
    }

    public long l;
    @Override
    public void onTick() {
        l++;
    }
}
