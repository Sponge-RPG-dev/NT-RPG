package cz.neumimto.effects.negative;

import cz.neumimto.Decorator;
import cz.neumimto.model.MultiboltModel;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 6.7.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
public class MultiboltEffect extends SpongeEffectBase<MultiboltModel> {

	private final IEntity source;
	private final MultiboltModel model;

	public MultiboltEffect(IEffectConsumer consumer, IEntity source, MultiboltModel model) {
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
		Living entity = ((ISpongeEntity)getConsumer()).getEntity();
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
