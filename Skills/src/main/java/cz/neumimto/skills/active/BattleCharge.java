package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.SpeedBoost;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@ResourceLoader.Skill("ntrpg:battlecharge")
public class BattleCharge extends ActiveSkill {

	@Inject
	private EffectService effectService;

	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.DURATION, 7500, 100);
		settings.addNode(SkillNodes.RADIUS, 7500, 100);
		settings.addNode("speed-per-level", 0.9f, 0.01f);
		setIcon(ItemTypes.BANNER);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		double distSq = Math.pow(skillContext.getDoubleNodeValue(SkillNodes.RADIUS), 2);
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		float value = skillContext.getFloatNodeValue("speed-per-level");
		if (character.hasParty()) {
			for (IActiveCharacter pmember : character.getParty().getPlayers()) {
				if (pmember.getLocation().getPosition().distanceSquared(character.getLocation().getPosition()) <= distSq) {
					SpeedBoost sp = new SpeedBoost(pmember, duration, value);
					effectService.addEffect(sp, this);
				}
			}
		} else {
			SpeedBoost sp = new SpeedBoost(character, duration, value);
			effectService.addEffect(sp, this);
		}
		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}
}
