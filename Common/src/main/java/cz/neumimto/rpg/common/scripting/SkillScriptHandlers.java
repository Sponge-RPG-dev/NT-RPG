package cz.neumimto.rpg.common.scripting;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;

import static cz.neumimto.nts.annotations.ScriptMeta.*;

public interface SkillScriptHandlers {

    interface Active extends SkillScriptHandlers {
        @ScriptTarget
        SkillResult onCast(@NamedParam("caster") IActiveCharacter caster,
                           @NamedParam("context") PlayerSkillContext context);
    }

    interface Targetted extends SkillScriptHandlers  {
        @ScriptTarget
        SkillResult castOnTarget(@NamedParam("caster") IActiveCharacter caster,
                                 @NamedParam("context") PlayerSkillContext context,
                                 @NamedParam("target") IEntity target);
    }

    interface Passive extends SkillScriptHandlers {
        @ScriptTarget
        SkillResult init(@NamedParam("caster") IActiveCharacter caster,
                         @NamedParam("context") PlayerSkillContext context);
    }

}
