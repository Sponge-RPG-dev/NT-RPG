package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;


@ClassGenerator.Generate(id = "name")
public class MaxLifeBonus extends EffectBase {

    public static final String name = "Max life";

    public MaxLifeBonus(IEffectConsumer character, long duration, String level) {

        //todo
    }

}
