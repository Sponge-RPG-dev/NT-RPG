package cz.neumimto.effects.positive;


import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.common.stacking.DoubleEffectStackingStrategy;
import cz.neumimto.rpg.players.IActiveCharacter;

@ClassGenerator.Generate(id = "name")
public class DamageToMana extends EffectBase<Double> {

    public static final String name = "Damage to mana";

    public DamageToMana(IActiveCharacter character, long duration, double percentage) {
        super(name, character);
        setDuration(duration);
	    setValue(percentage);
        setStackable(true, new DoubleEffectStackingStrategy());
    }

    public DamageToMana(IActiveCharacter character, long duration, String value) {
        this(character, duration, Double.parseDouble(value));
    }
}
