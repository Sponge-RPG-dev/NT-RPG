package cz.neumimto.effects.positive;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by ja on 6.7.2017.
 */
@ClassGenerator.Generate(id = "name")
public class DodgeEffect extends EffectBase<Float> {

	public static final String name = "Dodge";

	public DodgeEffect(IActiveCharacter character, float chance) {
		super(name, character);
		setValue(chance);
		setStackable(true, new FloatEffectStackingStrategy());
	}
}
