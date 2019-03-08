package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.common.positive.SpeedBoost;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.item.ItemTypes;

/**
 * Created by NeumimTo on 23.12.2015.
 */
@ResourceLoader.Skill("ntrpg:speed")
public class SkillSpeed extends ActiveSkill {

	@Inject
	private EffectService effectService;

	public void init() {
		super.init();
		setDamageType(null);
		settings.addNode(SkillNodes.DURATION, 1000, 1500);
		settings.addNode(SkillNodes.AMOUNT, 0.1f, 0.05f);
		addSkillType(SkillType.MOVEMENT);
		setIcon(ItemTypes.LEATHER_BOOTS);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		long duration = skillContext.getLongNodeValue(SkillNodes.DURATION);
		float amount = skillContext.getFloatNodeValue(SkillNodes.AMOUNT);
		SpeedBoost sb = new SpeedBoost(character, duration, amount);
		effectService.addEffect(sb, this);
		skillContext.next(character, info, skillContext.result(SkillResult.OK));
	}
}
