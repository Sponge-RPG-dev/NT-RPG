package cz.neumimto.rpg.api.skills.scripting;

import com.electronwill.nightconfig.core.Config;
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
    @Path("Spell")
    private List<Config> spell;

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

    public List<Config> getSpell() {
        return spell;
    }

    public void setSpell(List<Config> spell) {
        this.spell = spell;
    }

    public String getSuperType() {
        return superType;
    }

    public void setSuperType(String superType) {
        this.superType = superType;
    }
}
