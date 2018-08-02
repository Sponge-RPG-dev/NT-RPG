package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.effects.positive.ArrowstormEffect;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ActiveSkill;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.utils.SkillModifier;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.item.ItemTypes;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@ResourceLoader.Skill("ntrpg:arrowstorm")
public class Arrowstorm extends ActiveSkill {

	@Inject
	private EffectService effectService;

	@Override
	public void init() {
		super.init();
		setDamageType(DamageTypes.PROJECTILE);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DAMAGE, 10, 10);
		settings.addNode("min-arrows", 35, 1);
		settings.addNode("max-arrows", 45, 1);
		settings.addNode(SkillNodes.PERIOD, 100, -10);
		super.settings = settings;
		addSkillType(SkillType.PHYSICAL);
		addSkillType(SkillType.SUMMON);
		addSkillType(SkillType.PROJECTILE);
		setIcon(ItemTypes.ARROW);
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
