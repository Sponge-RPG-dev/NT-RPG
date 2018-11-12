package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.AllSkillsBonus;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.Targetted;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.mods.SkillContext;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 10.8.17.
 */
@ResourceLoader.Skill("ntrpg:dementia")
public class Dementia extends Targetted {

	@Inject
	private EntityService entityService;

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 30000, 1500);
		settings.addNode("skill-level", 1, 2);
		setSettings(settings);
		addSkillType(SkillType.DISEASE);
		setIcon(ItemTypes.ROTTEN_FLESH);
	}

	@Override
	public void castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info, SkillContext skillContext) {
		IEntity iEntity = entityService.get(target);
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		int skillLevel = skillContext.getIntNodeValue("skill-level");
		AllSkillsBonus bonus = new AllSkillsBonus(iEntity, duration, -1 * skillLevel);
		effectService.addEffect(bonus, iEntity, this);
		skillContext.next(source, info, SkillResult.OK);
	}
}
