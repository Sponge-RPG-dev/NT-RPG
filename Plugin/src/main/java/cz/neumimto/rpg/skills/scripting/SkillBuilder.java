package cz.neumimto.rpg.skills.scripting;


import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.text.Text;

import java.util.*;

public class SkillBuilder {
    
    private Text name;
    private String id;
    private SkillBuilder builder;
    private Text description;
    private Text lore;
    private Map<String, String> param = new HashMap<>();
    private List<SkillPipelineBuilder> pipelines = new ArrayList<>();
    private Set<SkillType> skillTypes = new HashSet<>();
    public static SkillBuilder create(String id) {
        SkillBuilder builder = new SkillBuilder();
        builder.id = id;
        return builder;
    }
    
    public SkillBuilder name(Text name) {
        this.name = name;
        return this;
    }

    public SkillBuilder name(String name) {
        return name(TextHelper.parse(name));
    }

    public SkillBuilder description(Text description) {
        this.description = description;
        return this;
    }

    public SkillBuilder description(String description) {
        return description(TextHelper.parse(description));
    }

    public SkillBuilder lore(Text lore) {
        this.lore = lore;
        return this;
    }

    public SkillBuilder lore(String lore) {
        return lore(TextHelper.parse(lore));
    }

    public SkillBuilder param(String key, String value, String valuePerLevel) {
        param.put(key, value);
        param.put(key + "_levelbonus", valuePerLevel);
        return this;
    }

    public SkillPipelineBuilder pipeline() {
        return new SkillPipelineBuilder(this);
    }

    public SkillBuilder addSkillType(SkillType skillType) {
        skillTypes.add(skillType);
        return this;
    }
}
