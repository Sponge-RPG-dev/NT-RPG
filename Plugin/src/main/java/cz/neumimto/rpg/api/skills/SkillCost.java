package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.sponge.skills.SkillItemCost;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;

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
