package cz.neumimto.skills.active;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import java.util.Optional;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:teleport")
public class SkillTeleport extends ActiveSkill<ISpongeCharacter> {

    @Override
    public void init() {
        super.init();
        settings.addNode(SkillNodes.RANGE, 20, 20);
        addSkillType(SkillType.TELEPORT);
    }

    @Override
    public void cast(ISpongeCharacter character, PlayerSkillContext playerSkillContext, SkillContext skillContext) {
        Player player = character.getPlayer();
        double doubleNodeValue = skillContext.getDoubleNodeValue(SkillNodes.RANGE);

        Optional<BlockRayHit<World>> optHit =
                BlockRay.from(player).distanceLimit(doubleNodeValue).stopFilter(Utils.SKILL_TARGET_BLOCK_FILTER).build().end();
        if (optHit.isPresent()) {
            Vector3d lookPos = optHit.get().getBlockPosition().toDouble();
            Location<World> worldLocation = new Location<>(player.getWorld(), lookPos);
            TeleportHelper helper = Sponge.getGame().getTeleportHelper();
            Optional<Location<World>> safeLocation = helper.getSafeLocation(worldLocation);
            if (safeLocation.isPresent()) {
                player.setLocation(safeLocation.get());
            }
        }
        skillContext.next(character, playerSkillContext, skillContext.result(SkillResult.OK));
    }
}
