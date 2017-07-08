package cz.neumimto.effects.negative;

import cz.neumimto.model.MultiboltModel;
import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.skills.NDamageType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;

/**
 * Created by NeumimTo on 6.7.2017.
 */
public class MultiboltEffect extends EffectBase<MultiboltModel> {

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
	public void onApply() {
		super.onApply();
		damage();
	}

	@Override
	public void onTick() {
		super.onTick();
		if (model.timesToHit <= 0) {
			setDuration(0);
		} else {
			damage();
		}
	}

	public void damage() {
		Living entity = getConsumer().getEntity();
		SkillDamageSourceBuilder build = new SkillDamageSourceBuilder();
		build.setEffect(this);
		build.setCaster(source);
		build.type(NDamageType.LIGHTNING);
		entity.damage(model.damage, build.build());
		Entity q = entity.getLocation().getExtent().createEntity(EntityTypes.LIGHTNING, entity.getLocation().getPosition());
		entity.getLocation().getExtent().spawnEntity(q, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
		model.timesToHit--;
	}
}
