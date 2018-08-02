package cz.neumimto.rpg.skills.scripting;

import java.util.HashMap;
import java.util.Map;

public class SkillPipelineContext {
    public static String EFFECT = "effect";
    public static String BROADCAST_ALL = "broadcast-all";

    private SkillExecutorContext rootContext;
    private Map<String, Object> param = new HashMap<>();

    public SkillPipelineContext(SkillExecutorContext rootContext) {
        this.rootContext = rootContext;
    }

    public SkillExecutorContext getRootContext() {
        return rootContext;
    }

    public Map<String, Object> getParam() {
        return param;
    }
}
