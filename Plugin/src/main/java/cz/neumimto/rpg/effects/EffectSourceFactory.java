package cz.neumimto.rpg.effects;

import cz.neumimto.rpg.commands.CommandBase;
import cz.neumimto.rpg.players.groups.ClassDefinition;
import cz.neumimto.rpg.skills.ISkill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeumimTo on 31.3.17.
 */
public class EffectSourceFactory {

	public static <T> EffectValue<List<T>> createEffectSource(ISkill iSkill, T t) {
		List<T> set = new ArrayList<T>();
		set.add(t);
		return new EffectValue<>(iSkill, set);
	}

	public static <T> EffectValue<T> createEffectSource(ClassDefinition classDefinition, T t) {
		return new EffectValue<>(classDefinition, t);
	}

	public static <T> EffectValue<List<T>> createEffectSource(CommandBase commandBase, T t) {
		List<T> set = new ArrayList<T>();
		set.add(t);
		return new EffectValue<>(commandBase, set);
	}


}
