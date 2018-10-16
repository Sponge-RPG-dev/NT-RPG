package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.parents.ActiveSkill;

import java.util.ArrayList;
import java.util.Comparator;


/**
 * Created by fs on 20.10.16.
 */
public class SkillContext {

	private final ArrayList<ActiveSkillPreProcessorWrapper> wrappers = new ArrayList<>();
	private int cursor;

	public SkillContext(ActiveSkill activeSkill) {
		cursor = -1;
		wrappers.add(new ActiveSkillPreProcessorWrapper(PreProcessorTarget.EXECUTION) {

			@Override
			public SkillResult doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillResult skillResult) {
				return activeSkill.cast(character, info, SkillContext.this);
			}
		});
	}


	public void sort() {
		wrappers.sort(Comparator.comparing(ActiveSkillPreProcessorWrapper::getTarget));
		wrappers.add(new ActiveSkillPreProcessorWrapper(PreProcessorTarget.LATEST) {
			@Override
			public SkillResult doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillResult skillResult) {
				return skillResult;
			}
		});
	}

	public SkillResult next(IActiveCharacter consumer, ExtendedSkillInfo info, SkillResult skillResult) {
		cursor++;
		return wrappers.get(cursor).doNext(consumer, info, skillResult);
	}

}
