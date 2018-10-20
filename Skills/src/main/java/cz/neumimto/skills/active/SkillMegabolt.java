package cz.neumimto.skills.active;

import cz.neumimto.Decorator;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;

import java.util.Set;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@ResourceLoader.Skill("ntrpg:megabolt")
public class SkillMegabolt extends ActiveSkill {

	public void init() {
		super.init();
		setDamageType(NDamageType.LIGHTNING);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DAMAGE, 10, 10);
		settings.addNode(SkillNodes.RADIUS, 30, 5);
		super.settings = settings;
		addSkillType(SkillType.AOE);
		addSkillType(SkillType.ELEMENTAL);
		addSkillType(SkillType.LIGHTNING);
	}

	@Override
	public void cast(IActiveCharacter iActiveCharacter, ExtendedSkillInfo extendedSkillInfo, SkillContext skillContext) {
		int r = getIntNodeValue(extendedSkillInfo, SkillNodes.RADIUS);
		Set<Entity> nearbyEntities = Utils.getNearbyEntities(iActiveCharacter.getPlayer().getLocation(), r);
		float damage = getFloatNodeValue(extendedSkillInfo, SkillNodes.DAMAGE);
		SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();
		builder.fromSkill(this);
		builder.setCaster(iActiveCharacter);
		SkillDamageSource src = builder.build();
		for (Entity e : nearbyEntities) {
			if (Utils.isLivingEntity(e)) {
				Living l = (Living) e;
				if (Utils.canDamage(iActiveCharacter, l)) {
					l.damage(damage, src);
					Decorator.strikeLightning(l);
				}
			}
		}
		skillContext.next(iActiveCharacter, extendedSkillInfo, skillContext.result(SkillResult.OK));
	}
}
