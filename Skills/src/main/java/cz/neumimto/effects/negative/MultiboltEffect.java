package cz.neumimto.effects.negative;

import cz.neumimto.Decorator;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.model.MultiboltModel;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.scripting.JsBinding;
import cz.neumimto.rpg.skills.NDamageType;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class MultiboltEffect extends EffectBase<MultiboltModel> {

	private final IEntity source;
	private final MultiboltModel model;

	public MultiboltEffect(IEffectConsumer consumer, IEntity source, @Inject MultiboltModel model) {
		super("Multibolt", consumer);
		this.source = source;
		this.model = model;
		setPeriod(1000);
		setDuration(getPeriod() * model.timesToHit);
	}

	@Override
	public void onApply(IEffect self) {
		super.onApply(self);
		damage();
	}

	@Override
	public void onTick(IEffect self) {
		if (model.timesToHit <= 0) {
			setDuration(0);
		} else {
			damage();
		}
	}

	public void damage() {
		Living entity = getConsumer().getEntity();
		SkillDamageSource s = new SkillDamageSourceBuilder()
				.setEffect(this)
				.setSource(source)
				.type(NDamageType.LIGHTNING)
				.build();
		entity.damage(model.damage, s);
		Decorator.strikeLightning(entity);
		model.timesToHit--;
	}
}
