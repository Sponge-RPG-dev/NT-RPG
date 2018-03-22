package cz.neumimto.skills.active;

import cz.neumimto.Decorator;
import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.Bleeding;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 5.8.2017.
 */
@ResourceLoader.Skill
public class Bandage extends Targetted {

	@Inject
	private EntityService entityService;

	@Inject
	private EffectService effectService;

	public Bandage() {
		setName(SkillLocalization.SKILL_BANDAGE_NAME);
		setDescription(SkillLocalization.Skill_BANDAGE_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.HEALED_AMOUNT, 15, 5);
		setSettings(settings);
		setIcon(ItemTypes.PAPER);
		addSkillType(SkillType.HEALING);
		addSkillType(SkillType.PHYSICAL);
	}

	@Override
	public SkillResult castOn(Living target, IActiveCharacter source, ExtendedSkillInfo info) {
		IEntity iEntity = entityService.get(target);
		if (iEntity.isFriendlyTo(source)) {
			float floatNodeValue = getFloatNodeValue(info, SkillNodes.HEALED_AMOUNT);
			entityService.healEntity(iEntity, floatNodeValue, this);
			Decorator.healEffect(iEntity.getLocation());
			if (iEntity.hasEffect(Bleeding.name)) {
				effectService.removeEffectContainer(iEntity.getEffect(Bleeding.name), iEntity);
			}
			return SkillResult.OK;
		}
		return SkillResult.CANCELLED;
	}
}
