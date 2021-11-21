package cz.neumimto.rpg.common.skills;

import com.electronwill.nightconfig.core.conversion.Path;
import cz.neumimto.rpg.common.skills.scripting.ScriptEffectModel;
import cz.neumimto.rpg.common.skills.scripting.ScriptListenerModel;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;

import java.util.ArrayList;
import java.util.List;

public class SkillsDefinition {

    @Path("Skills")
    public List<ScriptSkillModel> skills = new ArrayList<>();

    @Path("Effects")
    public List<ScriptEffectModel> effects = new ArrayList<>();

    @Path("Listeners")
    public List<ScriptListenerModel> listeners = new ArrayList<>();
}
