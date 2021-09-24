package cz.neumimto.rpg.api.scripting;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.scripting.ActiveScriptSkill;

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
