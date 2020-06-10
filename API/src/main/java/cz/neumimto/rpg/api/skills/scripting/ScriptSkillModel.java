package cz.neumimto.rpg.api.skills.scripting;

import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Config;
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
}
