package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.common.negative.SlowPotion;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.Targetted;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.living.Living;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@ResourceLoader.Skill("ntrpg:slow")
public class Slow extends Targetted {

	@Inject
	private EntityService entityService;

	@Inject
	private EffectService effectService;

	public Slow() {
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 5000, 100);
		settings.addNode(SkillNodes.AMPLIFIER, 1, 2);
		setSettings(settings);
	}

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info) {
		if (Utils.canDamage(source, target)) {
			long duration = getLongNodeValue(info, SkillNodes.DURATION);
			IEntity iEntity = entityService.get(target);
			int i = getIntNodeValue(info, SkillNodes.AMPLIFIER);
			SlowPotion effect = new SlowPotion(iEntity, duration, i);
			effectService.addEffect(effect, iEntity, this);
			return SkillResult.OK;
		} else {
			return SkillResult.NO_TARGET;
		}
	}
}
