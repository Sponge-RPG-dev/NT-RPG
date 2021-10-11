package cz.neumimto.rpg.common.scripting.mechanics;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta.Function;
import cz.neumimto.nts.annotations.ScriptMeta.Handler;
import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;

@AutoService(NTScriptProxy.class)
public class SkillCommons implements NTScriptProxy {

    @Handler
    @Function("config_value")
    public double configValue(
            @NamedParam("ctx") PlayerSkillContext skillContext,
            @NamedParam("key") String key
    ) {
        return skillContext.getDoubleNodeValue(key);
    }

}
