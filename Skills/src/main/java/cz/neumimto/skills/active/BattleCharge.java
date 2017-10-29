package cz.neumimto.skills.active;

import cz.neumimto.SkillLocalization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.common.positive.SpeedBoost;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.*;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

/**
 * Created by NeumimTo on 6.8.2017.
 */
@ResourceLoader.Skill
public class BattleCharge extends ActiveSkill {

	@Inject
	private EffectService effectService;

	public BattleCharge() {
		setName(SkillLocalization.SKILL_BATTLECHARGE_NAME);
		setDescription(SkillLocalization.SKILL_BATTLECHARGE_DESC);
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.DURATION, 7500, 100);
		settings.addNode(SkillNodes.RADIUS, 7500, 100);
		settings.addNode("speed-per-level", 0.9f, 0.01f);
		setIcon(ItemTypes.BANNER);
		setSettings(settings);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		if (character.getParty().getPlayers().size() == 1) {
			Gui.sendNotification(character, Text.builder(Localization.NO_PARTYMEMBERS)
					.color(TextColors.GOLD).style(TextStyles.BOLD).build());
			return SkillResult.CANCELLED;
		}

		double distSq = Math.pow(getDoubleNodeValue(info, SkillNodes.RADIUS), 2);
		long duration = getLongNodeValue(info, SkillNodes.DURATION);
		float value = getFloatNodeValue(info, "speed-per-level");
		boolean found = false;
		for (IActiveCharacter pmember : character.getParty().getPlayers()) {
			if (pmember.getLocation().getPosition().distanceSquared(character.getLocation().getPosition()) <= distSq) {
				found = true;
				SpeedBoost sp = new SpeedBoost(character, duration, value);
				effectService.addEffect(sp, character, this);
			}
		}

		if (!found) {
			Gui.sendNotification(character, Text.builder(Localization.NO_PARTYMEMBERS)
					.color(TextColors.GOLD).style(TextStyles.BOLD).build());
			return SkillResult.CANCELLED;
		}
		return SkillResult.OK;
	}
}
