package cz.neumimto.skills;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 14.2.2015.
 */
public class SkillInfo {
    public static SkillInfo EMPTY = new SkillInfo("Empty") {{
        setSkillSettings(new SkillSettings());
    }};
    private final String skill;
    private SkillSettings skillSettings;
    private int minPlayerLevel;
    private int maxSkillLevel;
    private Set<SkillInfo> softDepends = new HashSet<>();
    private Set<SkillInfo> hardDepends = new HashSet<>();
    private Set<SkillInfo> conflicts = new HashSet<>();
    private Set<SkillInfo> depending = new HashSet<>();

    public SkillInfo(String skill) {
        this.skill = skill;
    }

    public String getSkillName() {
        return skill;
    }

    public boolean conflictsWith(SkillInfo skillInfo) {
        return getConflicts().contains(skillInfo.getSkillName());
    }

    public SkillSettings getSkillSettings() {
        return skillSettings;
    }

    public void setSkillSettings(SkillSettings skillSettings) {
        this.skillSettings = skillSettings;
    }

    public int getMinPlayerLevel() {
        return minPlayerLevel;
    }

    public void setMinPlayerLevel(int minPlayerLevel) {
        this.minPlayerLevel = minPlayerLevel;
    }

    public int getMaxSkillLevel() {
        return maxSkillLevel;
    }

    public void setMaxSkillLevel(int maxSkillLevel) {
        this.maxSkillLevel = maxSkillLevel;
    }

    public Set<SkillInfo> getSoftDepends() {
        return softDepends;
    }

    public Set<SkillInfo> getHardDepends() {
        return hardDepends;
    }

    public Set<SkillInfo> getConflicts() {
        return conflicts;
    }

    public void setSoftDepends(Set<SkillInfo> softDepends) {
        this.softDepends = softDepends;
    }

    public void setHardDepends(Set<SkillInfo> hardDepends) {
        this.hardDepends = hardDepends;
    }

    public void setConflicts(Set<SkillInfo> conflicts) {
        this.conflicts = conflicts;
    }

    public Set<SkillInfo> getDepending() {
        return depending;
    }
}
