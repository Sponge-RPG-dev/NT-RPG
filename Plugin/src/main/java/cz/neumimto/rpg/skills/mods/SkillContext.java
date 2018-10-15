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

	private final ArrayList<ActiveSkillProcessorWrapper> wrappers = new ArrayList<>();
	private int cursor;

	public SkillContext(ActiveSkill activeSkill) {
		cursor = -1;
		wrappers.add(new ActiveSkillProcessorWrapper(ModTargetExcution.EXECUTION) {

			@Override
			public SkillResult doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillResult skillResult) {
				return activeSkill.cast(character, info, SkillContext.this);
			}
		});
	}


	public void sort() {
		wrappers.sort(Comparator.comparing(ActiveSkillProcessorWrapper::getTarget));
		wrappers.add(new ActiveSkillProcessorWrapper(ModTargetExcution.LATEST) {
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
