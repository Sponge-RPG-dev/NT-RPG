package cz.neumimto.rpg.utils;

import cz.neumimto.core.localization.Arg;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.configuration.Localizations;
import org.spongepowered.api.text.Text;

import java.util.Map;

/**
 * Created by NeumimTo on 31.12.2015.
 */
public enum SkillTreeActionResult {
	NO_SKILLPOINTS(Localizations.NO_SKILLPOINTS),
	SKILL_ON_MAX_LEVEL(Localizations.SKILL_IS_ON_MAX_LEVEL),
	NOT_LEARNED_SKILL(Localizations.NOT_LEARNED_SKILL),
	SKILL_REQUIRES_HIGHER_LEVEL(Localizations.SKILL_REQUIRES_HIGHER_LEVEL),
	UPGRADED(Localizations.SKILL_UPGRADED),
	SKILL_IS_NOT_IN_A_TREE(Localizations.SKILL_NOT_IN_A_TREE),
	DOES_NOT_MATCH_CHAIN(Localizations.MISSING_SKILL_DEPENDENCIES),
	ALREADY_LEARNED(Localizations.SKILL_ALREADY_LEARNED),
	LEARNED(Localizations.SKILL_LEARNED),
	SKILL_CONFCLITS(Localizations.SKILL_CONFLICTS),
	UNKNOWN(""),
	INSUFFICIENT_LEVEL_GAP(Localizations.INSUFFICIENT_LEVEL_GAP),
	NO_ACCESS_TO_SKILL(Localizations.NO_ACCESS_TO_SKILL);

    public String message;

	SkillTreeActionResult(String a) {

		this.message = a;
	}

	public static class Data {
		private final Map<String, Object> variables;

		public Data(Map<String, Object> variables) {
			this.variables = variables;
		}

		public Text bind(String t) {
			return TextHelper.parse(t, Arg.arg(variables));
		}
	}
}
