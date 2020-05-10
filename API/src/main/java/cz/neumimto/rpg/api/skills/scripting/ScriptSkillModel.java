package cz.neumimto.rpg.api.skills.scripting;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
