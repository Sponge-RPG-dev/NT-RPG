package cz.neumimto.rpg.api.scripting;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;

public interface SkillScriptHandlers {

    interface Active extends SkillScriptHandlers {
        SkillResult cast(IActiveCharacter caster, PlayerSkillContext context);
    }

    interface Targetted extends SkillScriptHandlers  {
        SkillResult castOnTarget(IActiveCharacter caster, PlayerSkillContext context, IEntity target);
    }

    interface Passive extends SkillScriptHandlers {
        SkillResult init(IActiveCharacter caster, PlayerSkillContext context);
    }
}
