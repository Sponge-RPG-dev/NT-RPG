package cz.neumimto.skills.active;

import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targeted;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:empathy")
public class Empathy extends Targeted {

	@Inject
	private EntityService entityService;

	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.MULTIPLIER, 5, 10);
		settings.addNode("max-damage", 100, 10);
		setDamageType(DamageTypes.MAGIC);
	}

	@Override
	public void castOn(IEntity target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		Player entity = source.getEntity();
		Double max = entity.get(Keys.MAX_HEALTH).get();
		Double a = entity.get(Keys.HEALTH).get();
		a = max - a;
		a *= skillContext.getFloatNodeValue(SkillNodes.MULTIPLIER);
		max = skillContext.getDoubleNodeValue("max-damage");
		if (max > 0) {
			a = a < max ? max : a;
		}
		SkillDamageSource build = new SkillDamageSourceBuilder()
				.fromSkill(this)
				.setSource(source)
				.build();
		target.getEntity().damage(a, build);
		skillContext.next(source, info, SkillResult.OK);
	}
}
