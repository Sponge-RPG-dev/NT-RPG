package cz.neumimto.rpg.skills.utils;

import org.apache.commons.lang3.NotImplementedException;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by fs on 20.10.16.
 */
public class SkillModifier {


	private Map<SkillModifierType, SkillModifierData> map = new EnumMap<>(SkillModifierType.class);

	public static SkillModifier fromCommandArgs(String[] args) {
		SkillModifier mod = new SkillModifier();
		//todo
		return null;
	}

	public SkillModifierData getValueOf(SkillModifierType t) {
		throw new NotImplementedException("Not yet implemented");
	}
}
