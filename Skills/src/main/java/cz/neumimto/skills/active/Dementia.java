package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.AllSkillsBonus;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 10.8.17.
 */
@ResourceLoader.Skill
public class Dementia extends Targetted {

	@Inject
	private EntityService entityService;

	@Inject
	private EffectService effectService;

	public Dementia() {
		setName(SkillLocalization.SKILL_DEMENTIA_NAME);
		setDescription(SkillLocalization.SKILL_DEMENTIA_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 30000, 1500);
		settings.addNode("skill-level", 1, 2);
		setSettings(settings);
		addSkillType(SkillType.DISEASE);
		setIcon(ItemTypes.ROTTEN_FLESH);
	}

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info) {
		IEntity iEntity = entityService.get(target);
		long duration = getLongNodeValue(info, SkillNodes.DURATION);
		int skillLevel = getIntNodeValue(info, "skill-level");
		AllSkillsBonus bonus = new AllSkillsBonus(iEntity, duration, -1 * skillLevel);
		effectService.addEffect(bonus, iEntity, this);
		return null;
	}
}
