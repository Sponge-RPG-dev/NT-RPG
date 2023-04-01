package cz.neumimto.rpg.common.scripting;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;

import static cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import static cz.neumimto.nts.annotations.ScriptMeta.ScriptTarget;

public interface SkillScriptHandlers {

    interface Active extends SkillScriptHandlers {
        @ScriptTarget
        SkillResult onCast(@NamedParam("caster") ActiveCharacter caster,
                           @NamedParam("context") PlayerSkillContext context,
                           @NamedParam("this_skill") ISkill iSkill);
    }

    interface Targetted extends SkillScriptHandlers {
        @ScriptTarget
        SkillResult castOnTarget(@NamedParam("caster") ActiveCharacter caster,
                                 @NamedParam("context") PlayerSkillContext context,
                                 @NamedParam("target") IEntity target,
                                 @NamedParam("this_skill") ISkill iSkill);
    }

    interface Passive extends SkillScriptHandlers {
        @ScriptTarget
        void init(@NamedParam("caster") ActiveCharacter caster,
                  @NamedParam("context") PlayerSkillContext context,
                  @NamedParam("this_skill") ISkill iSkill);
    }

}
