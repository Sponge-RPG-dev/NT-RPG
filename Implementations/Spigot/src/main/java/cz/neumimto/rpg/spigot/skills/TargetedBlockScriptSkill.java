package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.scripting.ScriptSkillModel;
import cz.neumimto.rpg.common.skills.types.ScriptSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class TargetedBlockScriptSkill extends TargetedBlockSkill implements ScriptSkill {

    private ScriptSkillModel model;

    private SpigotSkillScriptHandlers.TargetedBlock handler;

    @Override
    public void setHandler(SkillScriptHandlers handler) {
        this.handler = (SpigotSkillScriptHandlers.TargetedBlock) handler;
    }

    @Override
    public ScriptSkillModel getModel() {
        return model;
    }

    @Override
    public void setModel(ScriptSkillModel model) {
        this.model = model;
    }

    @Override
    protected SkillResult castOn(Block block, BlockFace blockFace, ISpigotCharacter character, PlayerSkillContext skillContext) {
        SkillResult skillResult = handler.castOnBlock(character, skillContext, block, blockFace, skillContext.getSkill());
        return skillResult == null ? SkillResult.OK : skillResult;
    }
}
