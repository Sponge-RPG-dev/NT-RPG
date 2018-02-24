package cz.neumimto.rpg;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;

@ClassGenerator.Generate(id = "name")
public class TestEffect extends EffectBase<TestModel> {
    public static String name = "Test";

    public TestEffect(IEffectConsumer character, long duration, float testModel) {
        super(name, character);
        setDuration(duration);
       // setValue(testModel);
    }

}
