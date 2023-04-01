package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public interface SpigotSkillScriptHandlers {

    interface TargetedBlock extends SkillScriptHandlers {
        @ScriptMeta.ScriptTarget
        SkillResult castOnBlock(@ScriptMeta.NamedParam("caster") ActiveCharacter caster,
                                @ScriptMeta.NamedParam("context") PlayerSkillContext context,
                                @ScriptMeta.NamedParam("block") Block block,
                                @ScriptMeta.NamedParam("blockFace") BlockFace blockFace,
                                @ScriptMeta.NamedParam("this_skill") ISkill iSkill);
    }
}
