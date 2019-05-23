package cz.neumimto.skills.active;

import cz.neumimto.Decorator;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;

import javax.inject.Singleton;
import java.util.Set;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:megabolt")
public class SkillMegabolt extends ActiveSkill {

	@Override
	public void init() {
		super.init();
		setDamageType(NDamageType.LIGHTNING);
		settings.addNode(SkillNodes.DAMAGE, 10, 10);
		settings.addNode(SkillNodes.RADIUS, 30, 5);
		addSkillType(SkillType.AOE);
		addSkillType(SkillType.ELEMENTAL);
		addSkillType(SkillType.LIGHTNING);
	}

	@Override
	public void cast(IActiveCharacter caster, PlayerSkillContext info, SkillContext skillContext) {
		int r = skillContext.getIntNodeValue(SkillNodes.RADIUS);
		Set<Entity> nearbyEntities = Utils.getNearbyEntities(caster.getPlayer().getLocation(), r);
		float damage = skillContext.getFloatNodeValue(SkillNodes.DAMAGE);
		SkillDamageSource s = new SkillDamageSourceBuilder()
				.fromSkill(this)
				.setSource(caster)
				.build();
		for (Entity e : nearbyEntities) {
			if (Utils.isLivingEntity(e)) {
				Living l = (Living) e;
				if (Utils.canDamage(caster, l)) {
					l.damage(damage, s);
					Decorator.strikeLightning(l);
				}
			}
		}
		skillContext.next(caster, info, skillContext.result(SkillResult.OK));
	}
}
