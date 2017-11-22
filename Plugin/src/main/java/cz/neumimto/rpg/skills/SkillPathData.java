package cz.neumimto.rpg.skills;

import cz.neumimto.rpg.skills.SkillData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 16.8.17.
 */
public class SkillPathData extends SkillData {

    private Set<String> permissions = new HashSet<>();
    private int tier;
    private int skillPointsRequired;
    private Map<String, Integer> skillBonus = new HashMap<>();
    public SkillPathData(String name) {
        super(name);
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getSkillPointsRequired() {
        return skillPointsRequired;
    }

    public void setSkillPointsRequired(int skillPointsRequired) {
        this.skillPointsRequired = skillPointsRequired;
    }

    public void addSkillBonus(String skill, int levels) {
        skillBonus.put(skill, levels);
    }

    public Map<String, Integer> getSkillBonus() {
        return skillBonus;
    }


}
