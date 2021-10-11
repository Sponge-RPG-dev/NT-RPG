package cz.neumimto.rpg.common.skills.scripting;

import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

import java.util.List;

public class ScriptSkillModel {

    @Path("Id")
    private String id;

    @Path("Skill-Types")
    private List<String> skillTypes;

    @Path("Damage-Type")
    private String damageType;

    @Path("Handler")
    @Optional
    private String handlerId;

    @Optional
    @Path("Script")
    private String script;

    @Optional
    @Path("Supertype")
    private String superType;

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getSkillTypes() {
        return skillTypes;
    }

    public String getDamageType() {
        return damageType;
    }

    public String getSuperType() {
        return superType;
    }

    public void setSuperType(String superType) {
        this.superType = superType;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }
}
