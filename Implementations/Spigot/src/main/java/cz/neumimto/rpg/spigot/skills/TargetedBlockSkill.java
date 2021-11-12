package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TargetedBlockSkill extends ActiveSkill<ISpigotCharacter> {
    protected Set<Material> transparent;


    public TargetedBlockSkill() {
        transparent = Stream.of(Material.values()).filter(Material::isTransparent).collect(Collectors.toSet());
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        int range = skillContext.getIntNodeValue(SkillNodes.RANGE);


        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(transparent, range);
        if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) {

        }
        Block block = lastTwoTargetBlocks.get(1);
        Block adjacentBlock = lastTwoTargetBlocks.get(0);
        BlockFace blockFace = block.getFace(adjacentBlock);

        if (isValidBlock(block)) {
            return castOn(block, blockFace, character, skillContext);
        } else {
            return SkillResult.NO_TARGET;
        }
    }

    protected abstract SkillResult castOn(Block block, BlockFace blockFace, ISpigotCharacter character, PlayerSkillContext skillContext);

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
