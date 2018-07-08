package cz.neumimto.effects;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.players.IActiveCharacter;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@ClassGenerator.Generate(id = "name", description = "An effect whichgives manadrain to the target")
public class ManaDrainEffect extends EffectBase<Float> {

	public static final String name = "Mana Drain";

	public ManaDrainEffect(IActiveCharacter character, long duration, float value) {
		super(name, character);
		setDuration(duration);
		setValue(value);
	}

}