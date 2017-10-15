package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.common.negative.SlowPotion;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@ResourceLoader.Skill
public class Slow extends Targetted {

	@Inject
	private EntityService entityService;

	@Inject
	private EffectService effectService;

	public Slow() {
		setName(SkillLocalization.SKILL_SLOW_NAME);
		setDescription(SkillLocalization.SKILL_SLOW_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 5000, 100);
		setSettings(settings);
	}

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info) {
		if (Utils.canDamage(source, target)) {
			long duration = getLongNodeValue(info, SkillNodes.DURATION);
			IEntity iEntity = entityService.get(target);
			int i = getIntNodeValue(info, SkillNodes.AMPFLIER);
			SlowPotion effect = new SlowPotion(iEntity, duration, i);
			effectService.addEffect(effect, iEntity, this);
			return SkillResult.OK;
		} else {
			return SkillResult.NO_TARGET;
		}
	}
}
