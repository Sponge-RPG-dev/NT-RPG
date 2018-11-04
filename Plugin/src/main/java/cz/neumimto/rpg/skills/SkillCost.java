package cz.neumimto.rpg.skills;

import cz.neumimto.rpg.skills.mods.ActiveSkillPreProcessorWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 4.11.2018.
 */
public class SkillCost {
    private Set<SkillItemCost> itemCost = new HashSet<>();
    private Set<ActiveSkillPreProcessorWrapper> insufficientProcessors = new HashSet<>();

    public SkillCost() {
    }

    public Set<SkillItemCost> getItemCost() {
        return itemCost;
    }

    public Set<ActiveSkillPreProcessorWrapper> getInsufficientProcessors() {
        return insufficientProcessors;
    }
}
