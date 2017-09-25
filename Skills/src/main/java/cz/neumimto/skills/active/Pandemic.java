package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.negative.PandemicEffect;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.damage.SkillDamageSource;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;

import java.util.Set;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@ResourceLoader.Skill
public class Pandemic extends ActiveSkill {

	@Inject
	private EffectService effectService;

	@Inject
	private EntityService entityService;

	public Pandemic() {
		setName(SkillLocalization.SKILL_PANDEMIC_NAME);
		setDescription(SkillLocalization.SKILL_PANDEMIC_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.RADIUS, 10, 5);
		settings.addNode(SkillNodes.DURATION, 3000, 500);
		settings.addNode(SkillNodes.DAMAGE, 15, 3);
		settings.addNode(SkillNodes.PERIOD, 1500, -10);
		setSettings(settings);
		addSkillType(SkillType.AOE);
		addSkillType(SkillType.DISEASE);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		float damage = getFloatNodeValue(info, SkillNodes.DAMAGE);
		int radius = getIntNodeValue(info, SkillNodes.RADIUS);
		long period = getLongNodeValue(info, SkillNodes.PERIOD);
		long duration = getLongNodeValue(info, SkillNodes.DURATION);
		Set<Entity> nearbyEntities = Utils.getNearbyEntities(character.getLocation(), radius);
		for (Entity entity : nearbyEntities) {
			if (Utils.isLivingEntity(entity)) {
				IEntity iEntity = entityService.get(entity);
				if (Utils.canDamage(character, (Living) entity)) {
					PandemicEffect effect = new PandemicEffect(character, iEntity, damage, duration, period);
					SkillDamageSource build = new SkillDamageSourceBuilder()
							.fromSkill(this)
							.setEffect(effect)
							.setCaster(character)
							.build();
					effect.setDamageSource(build);
					effectService.addEffect(effect, iEntity, this);
				}
			}
		}
		return SkillResult.OK;
	}
}
