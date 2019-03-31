package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.ISkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.events.skill.SkillDamageEventLate;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targeted;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.Tristate;

/**
 * Created by NeumimTo on 5.2.2016.
 */
@ResourceLoader.ListenerClass
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
		setIcon(ItemTypes.ENDER_EYE);
		setDamageType(DamageTypes.MAGIC);
		addSkillType(SkillType.HEALTH_DRAIN);
	}

	@Override
	public void castOn(IEntity target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		SkillDamageSource s = new SkillDamageSourceBuilder()
				.fromSkill(this)
				.setSource(source)
				.build();
		float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		target.getEntity().damage(damage, s);
		skillContext.next(source, info, SkillResult.OK);
	}

	@Listener(order = Order.LAST)
	@IsCancelled(Tristate.TRUE)
	public void onDamage(SkillDamageEventLate event, @First ISkillDamageSource damageSource) {
		if (event.getSkill() != null && event.getSkill().getClass() == this.getClass()) {
			IEntity caster = damageSource.getSourceIEntity();
			entityService.healEntity(caster, (float) event.getDamage(), this);
		}
	}
}
