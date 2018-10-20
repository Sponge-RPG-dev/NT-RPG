package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.parents.AbstractSkill;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.parents.IActiveSkill;

import java.util.ArrayList;
import java.util.Comparator;


/**
 * Created by fs on 20.10.16.
 */
public class SkillContext {

	private final ArrayList<ActiveSkillPreProcessorWrapper> wrappers = new ArrayList<>();
	private int cursor;
	private SkillResult result;
	private boolean continueExecution;

	public SkillContext(IActiveSkill activeSkill) {
		cursor = -1;
		wrappers.add(new ActiveSkillPreProcessorWrapper(PreProcessorTarget.EXECUTION) {

			@Override
			public void doNext(IActiveCharacter character, ExtendedSkillInfo info, SkillContext skillResult) {
				activeSkill.cast(character, info, SkillContext.this);
			}
		});
	}

	public SkillContext() {

	}

	public void sort() {
		wrappers.sort(Comparator.comparing(ActiveSkillPreProcessorWrapper::getTarget));
	}

	public void next(IActiveCharacter consumer, ExtendedSkillInfo info, SkillContext skillResult) {
		cursor++;
		if (result == SkillResult.CANCELLED || skillResult.continueExecution) {
			wrappers.get(cursor).doNext(consumer, info, skillResult);
		}
	}

	public void next(IActiveCharacter consumer, ExtendedSkillInfo info, SkillResult skillResult) {
		next(consumer, info, result(skillResult));
	}


	public SkillContext result(SkillResult result) {
		this.result = result;
		return this;
	}

	public SkillResult getResult() {
		return result;
	}

	public boolean continueExecution() {
		return continueExecution;
	}

	public SkillContext continueExecution(boolean continueExecution) {
		this.continueExecution = continueExecution;
		return this;
	}
}
