package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.nts.annotations.ScriptMeta.Function;
import cz.neumimto.nts.annotations.ScriptMeta.Handler;
import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;


public class SkillCommons {

    @Handler
    @Function("config_value")
    public double configValue(
            @NamedParam("ctx") PlayerSkillContext skillContext,
            @NamedParam("key") String key
    ) {
        return skillContext.getDoubleNodeValue(key);
    }

}
