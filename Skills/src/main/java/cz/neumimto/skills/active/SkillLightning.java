package cz.neumimto.skills.active;

import cz.neumimto.Decorator;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targetted;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@ResourceLoader.Skill("ntrpg:lightning")
public class SkillLightning extends Targetted {

	@Inject
	EntityService entityService;

	public void init() {
		super.init();
		setDamageType(NDamageType.LIGHTNING);
		SkillSettings skillSettings = new SkillSettings();
		skillSettings.addNode(SkillNodes.DAMAGE, 10, 20);
		skillSettings.addNode(SkillNodes.RANGE, 10, 10);
		super.settings = skillSettings;
		addSkillType(SkillType.ELEMENTAL);
		addSkillType(SkillType.LIGHTNING);
	}

	@Override
	public void castOn(Living target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		SkillDamageSourceBuilder build = new SkillDamageSourceBuilder();
		build.fromSkill(this);
		build.setCaster(source);
		target.damage(damage, build.build());
		Decorator.strikeLightning(target);
		skillContext.next(source, info, skillContext.result(SkillResult.OK));
	}
}
