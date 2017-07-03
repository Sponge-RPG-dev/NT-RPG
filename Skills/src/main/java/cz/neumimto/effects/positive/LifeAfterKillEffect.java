package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;

@ClassGenerator.Generate(id = "name")
public class LifeAfterKillEffect extends EffectBase<Float> {
    public static final String name = "Life after each kill";

    public LifeAfterKillEffect(IEffectConsumer character, long duration, String healedAmount) {
        super(name, character);
        setDuration(duration);
        setValue(Float.parseFloat(Utils.extractNumber(healedAmount)));
        setStackable(true, new FloatEffectStackingStrategy());
    }
}
