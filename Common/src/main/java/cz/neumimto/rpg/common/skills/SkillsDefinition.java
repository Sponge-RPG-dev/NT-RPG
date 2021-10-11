package cz.neumimto.rpg.common.skills;

import com.electronwill.nightconfig.core.conversion.Path;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;

import java.util.ArrayList;
import java.util.List;

public class SkillsDefinition {

    @Path("Skills")
    private List<ScriptSkillModel> skills = new ArrayList<>();

    public List<ScriptSkillModel> getSkills() {
        return skills;
    }
}
