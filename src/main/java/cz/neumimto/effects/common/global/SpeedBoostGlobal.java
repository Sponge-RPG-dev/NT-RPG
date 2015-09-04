package cz.neumimto.effects.common.global;

import cz.neumimto.effects.IEffectConsumer;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.effects.common.positive.SpeedBoost;
import cz.neumimto.players.IActiveCharacter;

/**
 * Created by NeumimTo on 6.8.2015.
 */
public class SpeedBoostGlobal implements IGlobalEffect<SpeedBoost> {
    @Override
    public SpeedBoost construct(IEffectConsumer consumer, long duration, int level) {
        return new SpeedBoost((IActiveCharacter) consumer, duration, level / 10);
    }

    @Override
    public String getName() {
        return SpeedBoost.name;
    }
}
