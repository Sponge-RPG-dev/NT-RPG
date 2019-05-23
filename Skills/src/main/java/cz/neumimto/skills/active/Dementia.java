package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.AllSkillsBonus;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.Targeted;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import org.spongepowered.api.item.ItemTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 10.8.17.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:dementia")
public class Dementia extends Targeted {

	@Inject
	private EntityService entityService;

	@Inject
	private EffectService effectService;

	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.DURATION, 30000, 1500);
		settings.addNode("skill-level", 1, 2);
		addSkillType(SkillType.DISEASE);
		setIcon(ItemTypes.ROTTEN_FLESH);
	}

	@Override
	public void castOn(IEntity target, IActiveCharacter source, PlayerSkillContext info, SkillContext skillContext) {
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		int skillLevel = skillContext.getIntNodeValue("skill-level");
		AllSkillsBonus bonus = new AllSkillsBonus(target, duration, -1 * skillLevel);
		effectService.addEffect(bonus, this);
		skillContext.next(source, info, SkillResult.OK);
	}
}
