package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.players.IActiveCharacter;


@ClassGenerator.Generate(id = "name")
public class MaxLifeBonus extends EffectBase {

    public static final String name = "Max. life bonus";

    public MaxLifeBonus(IActiveCharacter character, long duration, float level) {

        setStackable(false);
    }

}
