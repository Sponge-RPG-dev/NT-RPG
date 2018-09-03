package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.common.positive.SpeedBoost;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.utils.SkillModifier;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@ResourceLoader.Skill("ntrpg:battlecharge")
public class BattleCharge extends ActiveSkill {

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 7500, 100);
		settings.addNode(SkillNodes.RADIUS, 7500, 100);
		settings.addNode("speed-per-level", 0.9f, 0.01f);
		setIcon(ItemTypes.BANNER);
		setSettings(settings);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		double distSq = Math.pow(getDoubleNodeValue(info, SkillNodes.RADIUS), 2);
		long duration = getLongNodeValue(info, SkillNodes.DURATION);
		float value = getFloatNodeValue(info, "speed-per-level");
		if (character.hasParty()) {
			for (IActiveCharacter pmember : character.getParty().getPlayers()) {
				if (pmember.getLocation().getPosition().distanceSquared(character.getLocation().getPosition()) <= distSq) {
					SpeedBoost sp = new SpeedBoost(pmember, duration, value);
					effectService.addEffect(sp, pmember, this);
				}
			}
		} else {
			SpeedBoost sp = new SpeedBoost(character, duration, value);
			effectService.addEffect(sp, character, this);
		}
		return SkillResult.OK;
	}
}
