package cz.neumimto.rpg.common.scripting.mechanics;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta.Function;
import cz.neumimto.nts.annotations.ScriptMeta.Handler;
import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.IEntityType;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import jdk.jfr.Name;

@AutoService(NTScriptProxy.class)
public class SkillCommons implements NTScriptProxy {

    @Handler
    @Function("config_value")
    public double configValue(
            @NamedParam("c|ctx") PlayerSkillContext skillContext,
            @NamedParam("k|key") String key
    ) {
        return skillContext.getDoubleNodeValue(key);
    }

    @Handler
    @Function("exists")
    public boolean exists(@NamedParam("o|obj|var") Object o) {
        return o != null;
    }
}
