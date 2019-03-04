package cz.neumimto.effects.positive;

import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.common.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.entities.PropertyContainer;
import cz.neumimto.rpg.players.properties.DefaultProperties;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 29.3.17.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Increases resistance against fire, ice and lightning damage")
public class ElementalResistanceEffect extends EffectBase<Float> {

	public static final String name = "Elemental Resistance";

	public ElementalResistanceEffect(IEffectConsumer consumer, float percentage, long duration) {
		super(name, consumer);
		setStackable(true, new FloatEffectStackingStrategy());
		setValue(percentage);
		setDuration(duration);
	}

	@Override
	public void onApply(IEffect self) {
		super.onApply(self);
		apply(1, getConsumer());
	}

	private void apply(int i, PropertyContainer propertyContainer) {
		float characterProperty = propertyContainer.getProperty(DefaultProperties.fire_damage_protection_mult);
		propertyContainer.setProperty(DefaultProperties.fire_damage_protection_mult, characterProperty + getValue() * i);

		characterProperty = propertyContainer.getProperty(DefaultProperties.ice_damage_protection_mult);
		propertyContainer.setProperty(DefaultProperties.ice_damage_protection_mult, characterProperty + getValue() * i);

		characterProperty = propertyContainer.getProperty(DefaultProperties.lightning_damage_protection_mult);
		propertyContainer.setProperty(DefaultProperties.lightning_damage_protection_mult, characterProperty + getValue() * i);
	}

	@Override
	public void onRemove(IEffect self) {
		super.onRemove(self);
		apply(-1, getConsumer());
	}
}
