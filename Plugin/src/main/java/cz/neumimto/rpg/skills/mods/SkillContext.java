package cz.neumimto.rpg.skills.mods;

import com.google.common.collect.Maps;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.IActiveSkill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by fs on 20.10.16.
 */
public class SkillContext {

	protected final ArrayList<ActiveSkillPreProcessorWrapper> wrappers = new ArrayList<>();
	private ExtendedSkillInfo esi;
	private int cursor;
	private SkillResult result;
	private boolean continueExecution;
	private boolean copy;
	private Map<String, Float> skillNodes;
	public SkillContext(IActiveSkill activeSkill, ExtendedSkillInfo esi) {
		this.esi = esi;
		cursor = -1;
		continueExecution = true;
		skillNodes = esi.getSkillData().getSkillSettings().getNodes();
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

	public void addExecutor(ActiveSkillPreProcessorWrapper proc) {
		wrappers.add(proc);
	}

	public void addExecutor(Set<ActiveSkillPreProcessorWrapper> set) {
		wrappers.addAll(set);
	}

	public Map<String, Float> getSkillNodes() {
		return skillNodes;
	}

	public void overrideNode(String key, Float value) {
		if (!copy) {
			copy = true;
			skillNodes = new HashMap<>(skillNodes);
		}
		skillNodes.put(key, value);
	}
}
