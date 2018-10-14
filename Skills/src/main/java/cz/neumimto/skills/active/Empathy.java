package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.Targetted;
import cz.neumimto.rpg.skills.mods.SkillModList;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

/**
 * Created by NeumimTo on 7.7.2017.
 */
@ResourceLoader.Skill("ntrpg:empathy")
public class Empathy extends Targetted {

	@Inject
	private EntityService entityService;

	public void init() {
		super.init();
		super.settings = new SkillSettings();
		settings.addNode(SkillNodes.MULTIPLIER, 10, 10);
		settings.addNode("max-damage", -1, 0);
		setDamageType(DamageTypes.MAGIC);
	}

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info, SkillModList modifier) {
		Player entity = source.getEntity();
		Double max = entity.get(Keys.MAX_HEALTH).get();
		Double a = entity.get(Keys.HEALTH).get();
		a = max - a;
		a *= getFloatNodeValue(info, SkillNodes.MULTIPLIER, modifier);
		max = getDoubleNodeValue(info, "max-damage", modifier);
		if (max > 0) {
			a = a < max ? max : a;
		}
		SkillDamageSource build = new SkillDamageSourceBuilder()
				.fromSkill(this)
				.setTarget(entityService.get(target))
				.setCaster(source).build();
		target.damage(a, build);
		return SkillResult.CANCELLED;
	}
}
