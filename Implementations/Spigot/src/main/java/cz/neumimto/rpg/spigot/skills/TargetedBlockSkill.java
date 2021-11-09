package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public abstract class TargetedBlockSkill extends ActiveSkill<ISpigotCharacter> {

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        int range = skillContext.getIntNodeValue(SkillNodes.RANGE);

        Block block = rayTraceBlock(player, range);
        if (block != null && isValidBlock(block)) {
            return castOn(block, character, skillContext);
        } else {
            return SkillResult.NO_TARGET;
        }
    }

    protected abstract SkillResult castOn(Block block, ISpigotCharacter character, PlayerSkillContext skillContext);

    protected boolean isValidBlock(Block block) {
        return true;
    }


    public static Block rayTraceBlock(final Player player, final double maxDistance) {
        if (maxDistance <= 0.0) {
            return null;
        }
        RayTraceResult rayTraceResult = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection(), maxDistance, FluidCollisionMode.NEVER, true);
        if (rayTraceResult == null) {
            return null;
        }
        Block hitBlock = rayTraceResult.getHitBlock();
        return hitBlock;
    }
}
