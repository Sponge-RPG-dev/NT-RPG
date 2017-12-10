package cz.neumimto.rpg.utils;

import cz.neumimto.rpg.configuration.Localization;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by NeumimTo on 31.12.2015.
 */
public enum SkillTreeActionResult {
	NO_SKILLPOINTS(Localization.NO_SKILLPOINTS),
	SKILL_ON_MAX_LEVEL(Localization.SKILL_IS_ON_MAX_LEVEL),
	NOT_LEARNED_SKILL(Localization.NOT_LEARNED_SKILL),
	SKILL_REQUIRES_HIGHER_LEVEL(Localization.SKILL_REQUIRES_HIGHER_LEVEL),
	UPGRADED(Localization.SKILL_UPGRADED),
	SKILL_IS_NOT_IN_A_TREE(Localization.SKILL_NOT_IN_A_TREE),
	DOES_NOT_MATCH_CHAIN(Localization.MISSING_SKILL_DEPENDENCIES),
	ALREADY_LEARNED(Localization.SKILL_ALREADY_LEARNED),
	LEARNED(Localization.SKILL_LEARNED),
	SKILL_CONFCLITS(Localization.SKILL_CONFLICTS),
	UNKNOWN(""),
	NO_ACCESS_TO_SKILL(Localization.NO_ACCESS_TO_SKILL);
	public String message;

	SkillTreeActionResult(String a) {

		this.message = a;
	}

	public static class Data {
		public List<String> variables = new ArrayList<>();

		public Data(String... variables) {
			for (String s : variables) {
				this.variables.add(s);
			}
		}

		public Text bind(String t) {
			Iterator<String> iterator = variables.iterator();
			while (iterator.hasNext()) {
				String next = iterator.next();
				t = t.replace("%s", next);
			}
			return Text.of(t);
		}
	}
}
