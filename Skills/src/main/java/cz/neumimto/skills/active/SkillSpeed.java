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
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.mods.SkillModifier;
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
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 1000, 1500);
		settings.addNode(SkillNodes.AMOUNT, 0.1f, 0.05f);
		setSettings(settings);
		addSkillType(SkillType.MOVEMENT);
		setIcon(ItemTypes.LEATHER_BOOTS);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier skillModifier) {
		long duration = getLongNodeValue(info, SkillNodes.DURATION);
		float amount = getFloatNodeValue(info, SkillNodes.AMOUNT);
		SpeedBoost sb = new SpeedBoost(character, duration, amount);
		effectService.addEffect(sb, character, this);
		return SkillResult.OK;
	}
}
