package cz.neumimto.rpg.skills.utils;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by fs on 20.10.16.
 */
public class SkillModifier {


	private Map<SkillModifierType, SkillModifierData> map = new EnumMap<>(SkillModifierType.class);

	public SkillModifierData getValueOf(SkillModifierType t) {
		throw new NotImplementedException();
	}


	public static SkillModifier fromCommandArgs(String[] args) {
		SkillModifier mod = new SkillModifier();
		//todo
		return null;
	}
}
