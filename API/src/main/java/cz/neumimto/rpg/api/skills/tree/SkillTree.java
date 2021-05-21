

package cz.neumimto.rpg.api.skills.tree;

import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.utils.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 14.2.2015.
 */
public class SkillTree {

    public static final SkillTree Default;

    static {
        Default = new SkillTree();
        Default.setId("None");
    }

    private String id;

    private Map<String, SkillData> skills = new HashMap<>();

    private Map<Integer, SkillData> skillsById = new HashMap<>();

    private short[][] skillTreeMap;

    private Pair<Integer, Integer> center = new Pair<>(0, 0);

    public Map<String, SkillData> getSkills() {
        return skills;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SkillData getSkillById(int id) {
        return skillsById.get(id);
    }

    public SkillData getSkillById(String id) {
        return skills.get(id);
    }

    public short[][] getSkillTreeMap() {
        return skillTreeMap;
    }

    public void setSkillTreeMap(short[][] skillTreeMap) {
        this.skillTreeMap = skillTreeMap;
    }

    public Pair<Integer, Integer> getCenter() {
        return center;
    }

    public void setCenter(Pair<Integer, Integer> center) {
        this.center = center;
    }

    public void addSkill(SkillData value) {
        skills.put(value.getSkillId().toLowerCase(), value);
    }

    public void addSkillTreeId(SkillData value) {
        if (value.getSkillTreeId() > 0) {
            skillsById.put(value.getSkillTreeId(), value);
        }
    }
}
