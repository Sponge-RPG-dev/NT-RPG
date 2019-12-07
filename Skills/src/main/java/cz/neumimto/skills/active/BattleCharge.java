package cz.neumimto.skills.active;

import cz.neumimto.effects.positive.SpeedBoost;
import cz.neumimto.rpg.SpongeResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@Singleton
@SpongeResourceLoader.Skill("ntrpg:battlecharge")
public class BattleCharge extends ActiveSkill<ISpongeCharacter> {

	@Inject
	private EffectService effectService;

	@Override
	public void init() {
		super.init();
		settings.addNode(SkillNodes.DURATION, 7500, 100);
		settings.addNode(SkillNodes.RADIUS, 7500, 100);
		settings.addNode("speed-per-level", 0.9f, 0.01f);
	}

	@Override
	public void cast(ISpongeCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		double distSq = Math.pow(skillContext.getDoubleNodeValue(SkillNodes.RADIUS), 2);
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		float value = skillContext.getFloatNodeValue("speed-per-level");
		if (character.hasParty()) {
			for (ISpongeCharacter pmember : character.getParty().getPlayers()) {
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
