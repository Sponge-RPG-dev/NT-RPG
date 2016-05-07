package cz.neumimto.rpg.effects.common.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by fs on 21.9.15.
 */
@ClassGenerator.Generate(id = "name", inject = false)
public class ManacostReduction extends EffectBase {
    public static String name = "Manacost Reduction";

    public ManacostReduction(IActiveCharacter character, long duration) {
        super(name, character);
        setDuration(duration);
        setStackable(true);
    }


}
