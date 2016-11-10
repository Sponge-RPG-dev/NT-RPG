package cz.neumimto.rpg;


import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IGlobalEffect;

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
