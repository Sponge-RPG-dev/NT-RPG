package cz.neumimto.effects.positive;


import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.players.IActiveCharacter;

@ClassGenerator.Generate(id = "name")
public class DamageToMana extends EffectBase {

    public static final String name = "Damage to mana";

    private double value;
    public DamageToMana(IActiveCharacter character, long duration, float level) {

        this.value = level;
        setLevel((int) level);
        setStackable(false);
    }


    public double getValue() {
        return value;
    }
}
