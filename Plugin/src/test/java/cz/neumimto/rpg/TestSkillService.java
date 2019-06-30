package cz.neumimto.rpg;

import cz.neumimto.rpg.api.skills.scripting.ActiveScriptSkill;
import cz.neumimto.rpg.api.skills.types.PassiveScriptSkill;
import cz.neumimto.rpg.common.skills.SkillServiceimpl;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;

import javax.inject.Singleton;

@Singleton
public class TestSkillService extends SpongeSkillService {

    @Override
    public void init() {
        scriptSkillsParents.put("active", ActiveScriptSkill.class);
        scriptSkillsParents.put("passive", PassiveScriptSkill.class);
    }
}
