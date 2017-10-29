package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.WebEffect;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 20.8.2017.
 */
@ResourceLoader.Skill
public class Web extends Targetted {

	@Inject
	EntityService entityService;

	@Inject
	EffectService effectService;

	public Web() {
		setName(SkillLocalization.SKILL_WEB_NAME);
		setDescription(SkillLocalization.SKILL_WEB_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 5000, 100);
		setSettings(settings);
		setIcon(ItemTypes.WEB);
	}

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info) {
		long duration = getLongNodeValue(info, SkillNodes.DURATION);
		IEntity iEntity = entityService.get(target);
		WebEffect eff = new WebEffect(iEntity, duration);
		effectService.addEffect(eff, iEntity, this);
		return null;
	}


}
