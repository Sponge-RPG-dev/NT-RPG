package cz.neumimto.skills;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.ArrowstormEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@ResourceLoader.Skill
public class Arrowstorm extends ActiveSkill {

	@Inject
	private EffectService effectService;

	public Arrowstorm() {
		setDamageType(DamageTypes.PROJECTILE);
		setName("Arrowstorm");
		setDescription(SkillLocalization.Arrowstorm);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DAMAGE, 10, 10);
		settings.addNode("min-arrows", 5, 1);
		settings.addNode("max-arrows", 10, 1);
		settings.addNode(SkillNodes.PERIOD, 500, -10);
		super.settings = settings;
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		int min = getIntNodeValue(info, "min-arrows");
		int max = getIntNodeValue(info, "max-arrows");
		int arrows = ThreadLocalRandom.current().nextInt(max - min) + min;
		min = getIntNodeValue(info, SkillNodes.PERIOD);
		min = min <= 0 ? 1 : min;
		effectService.addEffect(new ArrowstormEffect(character, min, arrows), character, this);
		return SkillResult.OK;
	}
}
