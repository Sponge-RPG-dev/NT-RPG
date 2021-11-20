package cz.neumimto.rpg.common.skills.scripting;

import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

import java.util.List;

public class ScriptSkillModel {

    @Path("Id")
    public String id;

    @Path("Skill-Types")
    public List<String> skillTypes;

    @Path("Damage-Type")
    public String damageType;

    @Path("Handler")
    @Optional
    public String handlerId;

    @Optional
    @Path("Script")
    public String script;

    @Optional
    @Path("SuperType")
    public String superType;

}
