package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.ArrowstormEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.ItemTypes;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@ResourceLoader.Skill("ntrpg:arrowstorm")
public class Arrowstorm extends ActiveSkill {

	@Inject
	private EffectService effectService;

	@Override
	public void init() {
		super.init();
		setDamageType(DamageTypes.PROJECTILE);
		settings.addNode(SkillNodes.DAMAGE, 10, 10);
		settings.addNode("min-arrows", 35, 1);
		settings.addNode("max-arrows", 45, 1);
		settings.addNode(SkillNodes.PERIOD, 100, -10);
		addSkillType(SkillType.PHYSICAL);
		addSkillType(SkillType.SUMMON);
		addSkillType(SkillType.PROJECTILE);
		setIcon(ItemTypes.ARROW);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		int min = skillContext.getIntNodeValue("min-arrows");
		int max = skillContext.getIntNodeValue("max-arrows");
		int arrows = ThreadLocalRandom.current().nextInt(max - min) + min;
		min = skillContext.getIntNodeValue(SkillNodes.PERIOD);
		min = min <= 0 ? 1 : min;
		effectService.addEffect(new ArrowstormEffect(character, min, arrows), this);
		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}
}
