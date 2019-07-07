package cz.neumimto.skills.active;

import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.IResourceLoader;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.sponge.damage.ISkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSource;
import cz.neumimto.rpg.sponge.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.events.damage.SpongeEntitySkillDamageLateEvent;
import cz.neumimto.rpg.sponge.skills.types.Targeted;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.util.Tristate;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 5.2.2016.
 */
@Singleton
@IResourceLoader.ListenerClass
@ResourceLoader.Skill("ntrpg:brainsap")
public class BrainSap extends Targeted {

	@Inject
	private EntityService entityService;

	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.COOLDOWN, 1000f, 10f);
		settings.addNode(SkillNodes.RANGE, 10f, 1f);
		settings.addNode(SkillNodes.DAMAGE, 10f, 10f);
		setDamageType(DamageTypes.MAGIC.getId());
		addSkillType(SkillType.HEALTH_DRAIN);
	}

	@Override
	public void castOn(IEntity target, ISpongeCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		SkillDamageSource s = new SkillDamageSourceBuilder()
				.fromSkill(this)
				.setSource(source)
				.build();
		float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		((ISpongeEntity)target).getEntity().damage(damage, s);
		skillContext.next(source, info, SkillResult.OK);
	}

	@Listener(order = Order.LAST)
	@IsCancelled(Tristate.TRUE)
	public void onDamage(SpongeEntitySkillDamageLateEvent event, @First ISkillDamageSource damageSource) {
		if (event.getSkill() != null && event.getSkill().getClass() == this.getClass()) {
			IEntity caster = damageSource.getSourceIEntity();
			entityService.healEntity(caster, (float) event.getDamage(), this);
		}
	}

}
