package cz.neumimto.rpg.skills.mods;

import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ISkillNode;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.IActiveSkill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

import static cz.neumimto.rpg.Log.error;


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
	protected double staminacost;
	protected double manacost;
	protected double hpcost;

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

    private float getLevelNodeValue(String s, int level) {
        return getNodeValue(s) + level * getNodeValue(s + SkillSettings.bonus);
    }

    private float getNodeValue(String node) {
        Float aFloat = skillNodes.get(node.toLowerCase());
        if (aFloat == null) {
            error("Missing skill node " + node);
            return 0;
        }
        return aFloat;
    }

	public float getFloatNodeValue(ISkillNode node) {
		return getFloatNodeValue(node.value());
	}

	public float getFloatNodeValue(String node) {
		return getLevelNodeValue(node, esi.getTotalLevel());
	}

	public int getIntNodeValue(ISkillNode node) {
		return getIntNodeValue(node.value());
	}

	public int getIntNodeValue(String node) {
		return (int) getLevelNodeValue(node, esi.getTotalLevel());
	}

	public long getLongNodeValue(ISkillNode node) {
		return getLongNodeValue(node.value());
	}

	public long getLongNodeValue(String node) {
		return (long) getLevelNodeValue(node, esi.getTotalLevel());
	}

	public double getDoubleNodeValue(String node) {
		return getLevelNodeValue(node, esi.getTotalLevel());
	}

	public double getDoubleNodeValue(ISkillNode node) {
		return getDoubleNodeValue(node.value());
	}

	protected void setSkillCost(double hpcost, double manacost, double staminacost) {
		this.hpcost = hpcost;
		this.manacost = manacost;
		this.staminacost = staminacost;
	}
}
