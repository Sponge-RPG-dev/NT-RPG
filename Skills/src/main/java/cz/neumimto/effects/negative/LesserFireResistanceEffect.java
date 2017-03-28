package cz.neumimto.effects.negative;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;

/**
 * Created by ja on 28.3.2017.
 */
@ClassGenerator.Generate(id = "name")
public class LesserFireResistanceEffect extends EffectBase<LesserFireResistanceEffect> {

	public static final String name = "Decreased fire resistance";

	private float percentage;

	public LesserFireResistanceEffect(String name, IEffectConsumer consumer, float percentage, long duration) {
		super(name, consumer);
		setDuration(duration);
		this.percentage = percentage;
	}

	public LesserFireResistanceEffect(IActiveCharacter character, long duration, float level) {
		super(name, character);
		setDuration(duration);
		setPeriod(1000L);
		setStackable(true);
		this.percentage = level;
	}

	@Override
	public void onStack(LesserFireResistanceEffect effect) {
		onRemove();
		this.percentage += percentage + effect.percentage;
		onApply();
	}



	@Override
	public void onApply() {
		if (getConsumer().getEntity().getType() == EntityTypes.PLAYER) {
			IActiveCharacter character = (IActiveCharacter) getConsumer();
			float characterProperty = character.getCharacterProperty(DefaultProperties.fire_damage_protection_mult);
			character.setCharacterProperty(DefaultProperties.fire_damage_protection_mult, characterProperty - percentage);
		}
	}

	@Override
	public void onRemove() {
		if (getConsumer().getEntity().getType() == EntityTypes.PLAYER) {
			IActiveCharacter character = (IActiveCharacter) getConsumer();
			float characterProperty = character.getCharacterProperty(DefaultProperties.fire_damage_protection_mult);
			character.setCharacterProperty(DefaultProperties.fire_damage_protection_mult, characterProperty + percentage);
		}
	}

}

