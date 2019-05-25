package cz.neumimto.effects.positive;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.stacking.FloatEffectStackingStrategy;
import cz.neumimto.rpg.common.scripting.JsBinding;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.entities.PropertyContainer;
import cz.neumimto.rpg.sponge.properties.SpongeDefaultProperties;

/**
 * Created by NeumimTo on 29.3.17.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Increases resistance against fire, ice and lightning damage")
public class ElementalResistanceEffect extends EffectBase<Float> {

	public static final String name = "Elemental Resistance";

	public ElementalResistanceEffect(IEffectConsumer consumer, long duration, float percentage) {
		super(name, consumer);
		setStackable(true, FloatEffectStackingStrategy.INSTANCE);
		setValue(percentage);
		setDuration(duration);
	}

	@Override
	public void onApply(IEffect self) {
		super.onApply(self);
		apply(1, getConsumer());
	}

	private void apply(int i, PropertyContainer propertyContainer) {
		float characterProperty = propertyContainer.getProperty(SpongeDefaultProperties.fire_damage_protection_mult);
		propertyContainer.setProperty(SpongeDefaultProperties.fire_damage_protection_mult, characterProperty + getValue() * i);

		characterProperty = propertyContainer.getProperty(SpongeDefaultProperties.ice_damage_protection_mult);
		propertyContainer.setProperty(SpongeDefaultProperties.ice_damage_protection_mult, characterProperty + getValue() * i);

		characterProperty = propertyContainer.getProperty(SpongeDefaultProperties.lightning_damage_protection_mult);
		propertyContainer.setProperty(SpongeDefaultProperties.lightning_damage_protection_mult, characterProperty + getValue() * i);
	}

	@Override
	public void onRemove(IEffect self) {
		super.onRemove(self);
		apply(-1, getConsumer());
	}
}
