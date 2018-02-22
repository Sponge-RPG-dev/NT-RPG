package cz.neumimto.rpg;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;

public class TestEffect extends EffectBase<TestModel> {
    public static String name = "Test";

    public TestEffect(IEffectConsumer character, long duration, @Inject TestModel testModel) {
        super(name, character);
        setDuration(duration);
        setValue(testModel);
    }

}
