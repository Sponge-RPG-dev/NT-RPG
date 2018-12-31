package cz.neumimto.effects.positive;

import cz.neumimto.model.PotionEffectModel;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.EffectContainer;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.scripting.JsBinding;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 9.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class PotionEffect extends EffectBase<PotionEffectModel> {

	public static final String name = "Potion";

	public PotionEffect(IEffectConsumer consumer, long duration, PotionEffectModel model) {
		super(name, consumer);
		setDuration(duration);
		setValue(model);
		setStackable(true, null);
	}

	@Override
	public IEffectContainer<PotionEffectModel, PotionEffect> constructEffectContainer() {
		return new EffectContainer<PotionEffectModel, PotionEffect>(this) {
			@Override
			public void updateStackedValue() {
				setStackedValue(null);
				for (PotionEffect potionEffect : getEffects()) {
					if (getStackedValue() == null) {
						setStackedValue(potionEffect.getValue());
					} else {
						getStackedValue().mergeWith(potionEffect.getValue());
					}
				}
			}
		};
	}
}
