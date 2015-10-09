package cz.neumimto.effects.common.positive;

import cz.neumimto.effects.EffectBase;
import cz.neumimto.players.IActiveCharacter;

/**
 * Created by fs on 21.9.15.
 */
public class ManacostReduction extends EffectBase {
    public static String name = "Manacost reduction";

    public ManacostReduction(IActiveCharacter character, long duration) {
        setConsumer(character);
        setDuration(duration);
        setStackable(true);
    }


}
