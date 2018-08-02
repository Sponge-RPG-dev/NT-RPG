package cz.neumimto.rpg.skills.scripting;

import java.util.ArrayList;
import java.util.List;

public class SkillPipelineBuilder {
    private FindSkillTargetProcessor targetProcessor;
    private List<SkillAction> actionsPerTarget = new ArrayList<>();

    protected SkillBuilder skillBuilder;

    protected SkillPipelineBuilder(SkillBuilder skillBuilder) {
        this.skillBuilder = skillBuilder;
    }

    public SkillPipelineBuilder target(FindSkillTargetProcessor processor) {
        this.targetProcessor = processor;
        return this;
    }

    public SkillPipelineBuilder action(SkillAction action) {
        actionsPerTarget.add(action);
        return this;
    }

    public SkillBuilder endPipeline() {
        return skillBuilder;
    }
}
