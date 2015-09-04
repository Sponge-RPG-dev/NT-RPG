package cz.neumimto.skills;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 14.2.2015.
 */
public class SkillTree {
    public static SkillTree Default = new SkillTree() {{
        setId("None");
        setDescription("No skill tree");
    }};
    private String id;
    private Map<String, SkillInfo> skills = new HashMap<>();
    private String description;


    public Map<String, SkillInfo> getSkills() {
        return skills;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
